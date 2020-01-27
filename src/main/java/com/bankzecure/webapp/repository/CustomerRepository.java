package com.bankzecure.webapp.repository;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.bankzecure.webapp.entity.*;
import com.bankzecure.webapp.JdbcUtils;

public class CustomerRepository {
	private final static String DB_URL = "jdbc:mysql://localhost:3306/springboot_bankzecure?serverTimezone=GMT";
	private final static String DB_USERNAME = "bankzecure";
	private final static String DB_PASSWORD = "Ultr4B4nk@L0nd0n";

	public Customer findByIdentifierAndPassword(final String identifier, final String password) {
//    Connection connection = null;
//    Statement statement = null;
//    ResultSet resultSet = null;
//    try {
//      connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
//      statement = connection.createStatement();
//      final String query = "SELECT * FROM customer " +
//        "WHERE identifier = '" + identifier + "' AND password = '" + password + "'";
//      resultSet = statement.executeQuery(query);

		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

			prepareStatement = connection
					.prepareStatement("SELECT * FROM customer WHERE identifier =? AND password =?");

			prepareStatement.setString(1, identifier);
			prepareStatement.setString(2, password);

			resultSet = prepareStatement.executeQuery();

			Customer customer = null;

			if (resultSet.next()) {
				final int id = resultSet.getInt("id");
				final String identifierInDb = resultSet.getString("identifier");
				final String firstName = resultSet.getString("first_name");
				final String lastName = resultSet.getString("last_name");
				final String email = resultSet.getString("email");
				customer = new Customer(id, identifierInDb, firstName, lastName, email);
			}
			return customer;
		} catch (final SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeResultSet(resultSet);
			JdbcUtils.closeStatement(prepareStatement);
			JdbcUtils.closeConnection(connection);
		}
		return null;
	}

	public Customer update(String identifier, String newEmail, String newPassword) {

		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		Customer customer = null;
		try {
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

			if (newPassword != "") {
				prepareStatement = connection
						.prepareStatement("UPDATE customer SET email =?, password=? WHERE identifier =?");
				prepareStatement.setString(1, newEmail);
				prepareStatement.setString(2, newPassword);
				prepareStatement.setString(3, identifier);
			} else {
				prepareStatement = connection.prepareStatement("UPDATE customer SET email =? WHERE identifier =?");
				prepareStatement.setString(1, newEmail);
				prepareStatement.setString(2, identifier);
			}

			if (prepareStatement.executeUpdate() != 1) {
				throw new SQLException("failed to update data");
			}

			JdbcUtils.closeStatement(prepareStatement);
			JdbcUtils.closeConnection(connection);

			prepareStatement = null;
			connection = null;

			// Get Result Set from customer after update
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

			prepareStatement = connection.prepareStatement("SELECT * FROM customer WHERE identifier =?");

			prepareStatement.setString(1, identifier);

			resultSet = prepareStatement.executeQuery();

			if (resultSet.next()) {
				final int id = resultSet.getInt("id");
				final String identifierInDb = resultSet.getString("identifier");
				final String firstName = resultSet.getString("first_name");
				final String lastName = resultSet.getString("last_name");
				final String email = resultSet.getString("email");
				customer = new Customer(id, identifierInDb, firstName, lastName, email);
			}
			return customer;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeStatement(prepareStatement);
			JdbcUtils.closeConnection(connection);
		}
		return null;
	}

}