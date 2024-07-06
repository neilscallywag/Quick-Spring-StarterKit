// package com.starterkit.demo;


// import com.starterkit.demo.model.EnumRole;
// import com.starterkit.demo.model.Role;
// import com.starterkit.demo.repository.RoleRepository;

// import org.springframework.data.domain.Example;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
// import org.springframework.jdbc.core.JdbcTemplate;

// import javax.sql.DataSource;

// import java.util.List;
// import java.util.Optional;
// import java.util.function.Function;

// public class TestRoleRepository implements RoleRepository {

//     private final JdbcTemplate jdbcTemplate;

//     public TestRoleRepository(DataSource dataSource) {
//         this.jdbcTemplate = new JdbcTemplate(dataSource);
//     }

//     @Override
//     public Optional<Role> findByName(EnumRole name) {
//         try {
//             Role role = jdbcTemplate.queryForObject(
//                 "SELECT * FROM roles WHERE name = ?",
//                 new Object[]{name.name()},
//                 (rs, rowNum) -> new Role(rs.getLong("id"), EnumRole.valueOf(rs.getString("name")))
//             );
//             return Optional.ofNullable(role);
//         } catch (Exception e) {
//             return Optional.empty();
//         }
//     }

//     @Override
//     public Role save(Role role) {
//         jdbcTemplate.update("INSERT INTO roles (name) VALUES (?)", role.getName().name());
//         return role;
//     }

//     @Override
//     public void deleteAllByIdInBatch(Iterable<Integer> ids) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAllByIdInBatch'");
//     }

//     @Override
//     public void deleteAllInBatch() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAllInBatch'");
//     }

//     @Override
//     public void deleteAllInBatch(Iterable<Role> entities) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAllInBatch'");
//     }

//     @Override
//     public <S extends Role> List<S> findAll(Example<S> example) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public <S extends Role> List<S> findAll(Example<S> example, Sort sort) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public void flush() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'flush'");
//     }

//     @Override
//     public Role getById(Integer arg0) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getById'");
//     }

//     @Override
//     public Role getOne(Integer arg0) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getOne'");
//     }

//     @Override
//     public Role getReferenceById(Integer id) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getReferenceById'");
//     }

//     @Override
//     public <S extends Role> List<S> saveAllAndFlush(Iterable<S> entities) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'saveAllAndFlush'");
//     }

//     @Override
//     public <S extends Role> S saveAndFlush(S entity) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'saveAndFlush'");
//     }

//     @Override
//     public <S extends Role> List<S> saveAll(Iterable<S> entities) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'saveAll'");
//     }

//     @Override
//     public List<Role> findAll() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public List<Role> findAllById(Iterable<Integer> ids) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAllById'");
//     }

//     @Override
//     public Optional<Role> findById(Integer id) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findById'");
//     }

//     @Override
//     public boolean existsById(Integer id) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'existsById'");
//     }

//     @Override
//     public long count() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'count'");
//     }

//     @Override
//     public void deleteById(Integer id) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
//     }

//     @Override
//     public void delete(Role entity) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'delete'");
//     }

//     @Override
//     public void deleteAllById(Iterable<? extends Integer> ids) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAllById'");
//     }

//     @Override
//     public void deleteAll(Iterable<? extends Role> entities) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
//     }

//     @Override
//     public void deleteAll() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
//     }

//     @Override
//     public List<Role> findAll(Sort sort) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public Page<Role> findAll(Pageable pageable) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public <S extends Role> Optional<S> findOne(Example<S> example) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findOne'");
//     }

//     @Override
//     public <S extends Role> Page<S> findAll(Example<S> example, Pageable pageable) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public <S extends Role> long count(Example<S> example) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'count'");
//     }

//     @Override
//     public <S extends Role> boolean exists(Example<S> example) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'exists'");
//     }

//     @Override
//     public <S extends Role, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findBy'");
//     }

//     // Implement other methods if necessary
// }
