package wisepaas.datahub.java.sdk.common;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import wisepaas.datahub.java.sdk.common.Const.DataRecover;

public class DataRecoverHelper {

    private String _connString;

    private String _dbFilePath;
    private Object _lockObj;

    public DataRecoverHelper(String androidPackageName) {
        if (Helpers.isAndroid() == true) {
            _dbFilePath = "/data/data/" + androidPackageName + "/" + DataRecover.DatabaseFileName;
            _connString = "jdbc:sqldroid:" + _dbFilePath;

            try {
                DriverManager.registerDriver((Driver) Class.forName("org.sqldroid.SQLDroidDriver").newInstance());
            } catch (Exception e) {
                throw new RuntimeException("Failed to register SQLDroidDriver");
            }
        } else {
            _dbFilePath = new File("").getAbsolutePath() + File.separatorChar + DataRecover.DatabaseFileName;
            _connString = "jdbc:sqlite:" + _dbFilePath;
        }

        _lockObj = new Object();
    }

    public Boolean DataAvailable() {
        Boolean result = false;

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if (new File(_dbFilePath).exists() == false) {
                return false;
            }
            synchronized (_lockObj) {
                conn = DriverManager.getConnection(_connString);
                stmt = conn.createStatement();

                int dataCount = 0;
                rs = stmt.executeQuery("SELECT COUNT(*) FROM Data");
                while (rs.next()) {
                    dataCount = rs.getInt(1);
                }

                if (dataCount > 0) {
                    result = true;
                }

                if (result == false) {
                    conn.close();
                    conn = null;
                    File fdelete = new File(_dbFilePath);
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            System.out.println("file Deleted :" + _dbFilePath);
                        } else {
                            System.out.println("file not Deleted :" + _dbFilePath);
                        }
                    }
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    /* ignored */}
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    /* ignored */}
            }
        }
        return result;
    }

    private static String createDelQuery(int length) {
        String query = "DELETE FROM Data WHERE id IN (";
        StringBuilder queryBuilder = new StringBuilder(query);
        for (int i = 0; i < length; i++) {
            queryBuilder.append(" ?");
            if (i != length - 1)
                queryBuilder.append(",");
        }
        queryBuilder.append(")");
        return queryBuilder.toString();
    }

    public ArrayList<String> Read(Integer count) {
        count = count != null ? count : DataRecover.DEAFAULT_DATARECOVER_COUNT;

        ArrayList<String> messages = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            synchronized (_lockObj) {
                conn = DriverManager.getConnection(_connString);

                pstmt = conn.prepareStatement("SELECT * FROM Data LIMIT ?");
                pstmt.setInt(1, count);
                rs = pstmt.executeQuery();

                ArrayList<Integer> idList = new ArrayList<>();

                while (rs.next()) {
                    idList.add(rs.getInt("id"));
                    messages.add(rs.getString("message"));
                }

                if (idList.size() > 0) {
                    pstmt = conn.prepareStatement(createDelQuery(idList.size()));
                    for (int i = 0; i < idList.size(); i++) {
                        pstmt.setInt(i + 1, idList.get(i));
                    }

                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    /* ignored */}
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    /* ignored */}
            }
        }

        return messages;
    }

    public Boolean Write(ArrayList<String> payloads) {
        Boolean result = false;

        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;

        try {
            if (payloads.size() == 0) {
                return false;
            }

            synchronized (_lockObj) {
                conn = DriverManager.getConnection(_connString);
                System.out.println("[DataRecover] Conn success");

                for (String payload : payloads) {
                    int n = 0;
                    stmt = conn.createStatement();
                    stmt.execute(
                            "CREATE TABLE IF Not exists Data (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, message TEXT NOT NULL)");

                    pstmt = conn.prepareStatement("INSERT INTO Data (message) VALUES (?)");
                    pstmt.setString(1, payload);

                    n = pstmt.executeUpdate();

                    if (n != 1) {
                        return false;
                    }

                }

                System.out.println("[DataRecover] All data has been written into the sqlite");
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    /* ignored */}
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    /* ignored */}
            }
        }
        return result;
    }

}