package edu.persianlearningapp.dao;

import edu.persianlearningapp.dao.mysql.MySQLDAOFactory;

public final class DAOFactoryProvider {
    private DAOFactoryProvider() { }
    public static AbstractDAOFactory createFactory() { return new MySQLDAOFactory(); }
}
