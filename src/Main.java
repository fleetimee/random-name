import java.sql.*;
import java.util.Scanner;

public class Main {

    private static void updateData(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan ID yang mau diupdate: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline left-over

        String selectQuery = "SELECT * FROM mahasiswa WHERE id = ?";
        String updateQuery = "UPDATE mahasiswa SET nama = ?, alamat = ? WHERE id = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            // Get current data
            selectStmt.setInt(1, id);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    String oldNama = rs.getString("nama");
                    String oldAlamat = rs.getString("alamat");

                    System.out.println("Current Data:");
                    System.out.println("Nama: " + oldNama);
                    System.out.println("Alamat: " + oldAlamat);

                    System.out.print("Masukkan nama baru (kosongkan jika ingin menggunakan nama lama): ");
                    String newNama = scanner.nextLine();
                    System.out.print("Masukkan alamat baru (kosongkan jika ingin menggunakan alamat lama): ");
                    String newAlamat = scanner.nextLine();

                    newNama = newNama.isEmpty() ? oldNama : newNama;
                    newAlamat = newAlamat.isEmpty() ? oldAlamat : newAlamat;

                    updateStmt.setString(1, newNama);
                    updateStmt.setString(2, newAlamat);
                    updateStmt.setInt(3, id);

                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Data mahasiswa berhasil diupdate!");
                    }
                } else {
                    System.out.println("Tidak ada data untuk id " + id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void inputData(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan nama: ");
        String nama = scanner.nextLine();
        System.out.print("Masukkan alamat: ");
        String alamat = scanner.nextLine();

        String query = "INSERT INTO mahasiswa (nama, alamat) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nama);
            pstmt.setString(2, alamat);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Data berhasil ditambah");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showData(Connection connection) {
        String query = "SELECT * FROM mahasiswa";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            // Print column names
            for (int i = 1; i <= columnsNumber; i++) {
                System.out.print(rsmd.getColumnName(i) + "\t");
            }

            System.out.println("\n----------------------------------");

            // Print data
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }

            System.out.println("----------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mahasiswa?useSSL=false";
        String username = "root";
        String password = "";


        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected successfully!");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. Input Data");
                System.out.println("2. Tampil Data");
                System.out.println("3. Update Data");
                System.out.println("0. Exit");
                System.out.print("Pilihan: ");
                int option = scanner.nextInt();

                switch (option) {
                    case 1:
                        inputData(connection);
                        break;
                    case 2:
                        showData(connection);
                        break;
                    case 3:
                        updateData(connection);
                        break;
                    case 0:
                        System.out.println("Keluar...");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database!", e);
        }
    }
}