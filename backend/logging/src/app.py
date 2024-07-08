from flask import Flask, request, jsonify
import boto3
import time
import uuid
import json
import os

app = Flask(__name__)

S3_BUCKET = 'your-s3-bucket-name'
DYNAMODB_TABLE = 'Logs'
USE_S3 = os.getenv('USE_S3', 'false').lower() == 'true'
USE_DYNAMODB = os.getenv('USE_DYNAMODB', 'false').lower() == 'true'

s3_client = boto3.client('s3') if USE_S3 else None
dynamodb_client = boto3.resource('dynamodb') if USE_DYNAMODB else None
table = None

if dynamodb_client:
    try:
        table = dynamodb_client.create_table(
            TableName=DYNAMODB_TABLE,
            KeySchema=[
                {'AttributeName': 'log_id', 'KeyType': 'HASH'},  # Partition key
            ],
            AttributeDefinitions=[
                {'AttributeName': 'log_id', 'AttributeType': 'S'},
            ],
            ProvisionedThroughput={
                'ReadCapacityUnits': 5,
                'WriteCapacityUnits': 5
            }
        )
        table.meta.client.get_waiter('table_exists').wait(TableName=DYNAMODB_TABLE)
    except dynamodb_client.meta.client.exceptions.ResourceInUseException:
        table = dynamodb_client.Table(DYNAMODB_TABLE)

@app.route('/logs', methods=['POST'])
def receive_log():
    try:
        log_entry = request.json
        log_id = f"{str(uuid.uuid4())}_{int(time.time())}"
        timestamp = int(time.time())

        print(f"Log ID: {log_id}, Timestamp: {timestamp}, Message: {log_entry['message']}")

        if USE_DYNAMODB and table:
            table.put_item(
                Item={
                    'log_id': log_id,
                    'timestamp': timestamp,
                    'message': log_entry['message']
                }
            )

        if USE_S3 and s3_client:
            s3_client.put_object(
                Bucket=S3_BUCKET,
                Key=f'logs/{timestamp}_{log_id}.json',
                Body=json.dumps(log_entry)
            )

        return jsonify({"status": "success"}), 201
    except Exception as e:
        print(f"Error processing log: {str(e)}")
        return jsonify({"status": "error", "message": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
