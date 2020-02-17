package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.ProductDao;
import model.entities.Department;
import model.entities.Product;

public class ProductDaoJDBC implements ProductDao {

	private Connection conn;

	public ProductDaoJDBC(Connection conn) {
		this.conn = conn;

	}

	@Override
	public void insert(Product obj) {
		PreparedStatement st = null;

		try {
			st = (PreparedStatement) conn.prepareStatement("INSERT INTO product "
					+ "(code, name, description, price) " + "VALUES " + "(?, ?, ?, ? ) ",
					Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, obj.getCode());
			st.setString(2, obj.getName());
			st.setString(3, obj.getDescription());
			st.setDouble(4, obj.getPrice());
			

			int rowAsfected = st.executeUpdate();

			if (rowAsfected > 0) {
				ResultSet rs = st.getGeneratedKeys();

				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpected error! No rows affected!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void update(Product obj) {
		PreparedStatement st = null;

		try {

			st = (PreparedStatement) conn.prepareStatement("UPDATE product"
					+ "SET code = ? , name = ?, description = ?, price = ?, DepartmentId = ? " + "WHERE id = ?");

			st.setInt(1, obj.getCode());
			st.setString(2, obj.getName());
			st.setString(3, obj.getDescription());
			st.setDouble(4, obj.getPrice());
			st.setInt(5, obj.getDepartment().getId());

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("DELETE FROM product WHERE id = ?");

			st.setInt(1, id);
			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Product findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT product.*,product.name as DepName " + "FROM product INNER JOIN department "
							+ "ON product.DepartmentId = department.Id " + "WHERE product.id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();

			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				Product obj = instantiateProduct(rs, dep);
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

	private Product instantiateProduct(ResultSet rs, Department dep) throws SQLException {
		Product obj = new Product();
		obj.setId(rs.getInt("code"));
		obj.setName(rs.getString("name"));
		obj.setDescription(rs.getString("description"));
		obj.setPrice(rs.getDouble("price"));
		obj.setDepartment(dep);
		return obj;

	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Product> findAll() {

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM product ORDER BY Name");
			rs = st.executeQuery();

			List<Product> list = new ArrayList<>();

			while (rs.next()) {
				Product obj = new Product();
				obj.setId(rs.getInt("Id"));
				obj.setCode(rs.getInt("code"));
				obj.setName(rs.getString("Name"));
				obj.setDescription(rs.getNString("description"));
				obj.setPrice(rs.getDouble("price"));
				list.add(obj);
			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);

		}

	}

	@Override
	public List<Product> findByDepartment(Department department) {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT product.*,department.Name as DepName " + "FROM product INNER JOIN department "
							+ "ON product.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name");

			st.setInt(1, department.getId());
			rs = st.executeQuery();

			List<Product> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {

				Department dep = map.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Product obj = instantiateProduct(rs, dep);
				list.add(obj);
			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeStatement(st);
		}

	}

	@Override
	public List<Product> findByDepartment() {
		// TODO Auto-generated method stub
		return null;
	}

}
