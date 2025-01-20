package crud_db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JOptionPane;
import java.io.File;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public class crud_db {
    
    // Konfigurasi koneksi database
    private String jdbcURL = "jdbc:mysql://localhost:3306/2210010327_pertanian";
    private String username = "root";
    private String password = "";
    
    private DefaultTableModel Modelnya;
    private TableColumn Kolomnya;
    
    public crud_db(){}
    
    /**
     * Method untuk membuat koneksi ke database
     * @return Connection - Object koneksi database
     * @throws SQLException 
     */
    public Connection getKoneksiDB() throws SQLException {
        try {
            Driver mysqldriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(mysqldriver);
            System.out.println("Koneksi Berhasil");
        } catch (Exception e) {
            System.out.println("Error Koneksi: " + e.toString());
        }
        
        return DriverManager.getConnection(jdbcURL, username, password);
    }
    
    
    public boolean DuplicateKey(String NamaTabel, String PrimaryKey, String isiData) {
        boolean hasil = false;
        int jumlah = 0;
        try {
            String SQL = "SELECT * FROM " + NamaTabel + " WHERE " + PrimaryKey + " ='" + isiData + "'";
            Statement perintah = getKoneksiDB().createStatement();
            ResultSet hasilData = perintah.executeQuery(SQL);
            while (hasilData.next()) {
                jumlah++;
            }
            if (jumlah == 1) { hasil = true; } else { hasil = false; }
            
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return hasil;
    }

    public String getFieldTabel(String[] FieldTabelnya) {
        String hasilnya = "";
        int deteksiIndexAkhir = FieldTabelnya.length - 1;
        try {
            for (int i = 0; i < FieldTabelnya.length; i++) {
                if (i == deteksiIndexAkhir) {
                    hasilnya = hasilnya + FieldTabelnya[i];
                } else {
                    hasilnya = hasilnya + FieldTabelnya[i] + ",";
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return "(" + hasilnya + ")";
    }

    public String getIsiTabel(String[] IsiTabelnya) {
        String hasilnya = "";
        int DeteksiIndex = IsiTabelnya.length - 1;
        try {
            for (int i = 0; i < IsiTabelnya.length; i++) {
                if (i == DeteksiIndex) {
                    hasilnya = hasilnya + "'" + IsiTabelnya[i] + "'";
                } else {
                    hasilnya = hasilnya + "'" + IsiTabelnya[i] + "',";
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return "(" + hasilnya + ")";
    }
    
    public void SimpanDinamis(String NamaTabel, String[] Fieldnya, String[] Isinya) {
        try {
            // Validasi ukuran data untuk 'KodeDVD' sebelum menyimpan ke database
            int idIndex = -1;
            for (int i = 0; i < Fieldnya.length; i++) {
                if (Fieldnya[i].equals("id")) {
                    idIndex = i;
                    break;
                }
            }

            if (idIndex != -1 && Isinya[idIndex].length() > 5) { // Sesuaikan dengan panjang yang dibutuhkan
                JOptionPane.showMessageDialog(null, "Data kode_produk terlalu panjang. Maksimum 5 karakter.");
                return;
            }

            String SQLSave = "INSERT INTO " + NamaTabel + " " + getFieldTabel(Fieldnya) + " VALUES " + getIsiTabel(Isinya);
            Statement perintah = getKoneksiDB().createStatement();
            perintah.executeUpdate(SQLSave);
            perintah.close();
            JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    public String getFieldValueEdit(String[] Field, String[] value) {
        String hasil = "";
        int deteksi = Field.length - 1;
        try {
            for (int i = 0; i < Field.length; i++) {
                if (i == deteksi) {
                    hasil = hasil + Field[i] + " ='" + value[i] + "'";
                } else {
                    hasil = hasil + Field[i] + " ='" + value[i] + "',";
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
        return hasil;
    }
    
    public void UbahDinamis(String NamaTabel, String PrimaryKey, String IsiPrimary, String[] Field, String[] Value) {
        try {
            String SQLUbah = "UPDATE " + NamaTabel + " SET " + getFieldValueEdit(Field, Value) + " WHERE " + PrimaryKey + "='" + IsiPrimary + "'";
            Statement perintah = getKoneksiDB().createStatement();
            perintah.executeUpdate(SQLUbah);
            perintah.close();
            getKoneksiDB().close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void HapusDinamis(String NamaTabel, String PrimaryKey, String IsiPrimary) {
        try {
            String SQLHapus = "DELETE FROM " + NamaTabel + " WHERE " + PrimaryKey + "='" + IsiPrimary + "'";
            Statement perintah = getKoneksiDB().createStatement();
            perintah.executeUpdate(SQLHapus);
            perintah.close();
            getKoneksiDB().close();
            JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus");
        } catch (Exception e) {
          System.err.println(e.toString());
        }
    }
    
    public void settingLebarKolom(javax.swing.JTable tabel, int[] lebarKolom) {
    javax.swing.table.TableColumn column;
    tabel.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF); // Nonaktifkan auto resize
    for (int i = 0; i < lebarKolom.length; i++) {
        column = tabel.getColumnModel().getColumn(i);
        column.setPreferredWidth(lebarKolom[i]); // Atur lebar kolom sesuai array
    }
}

    
    public void settingJudulTabel(JTable Tabelnya, String[] JudulKolom){
        try {
            Modelnya = new DefaultTableModel();
            Tabelnya.setModel(Modelnya);
            for (int i = 0; i < JudulKolom.length; i++) {
                Modelnya.addColumn(JudulKolom[i]);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    public Object[][] isiTabel(String SQL, int jumlah) {
    Object[][] data = null;
    try {
        Connection koneksi = getKoneksiDB(); // Pastikan koneksi valid
        // Buat statement dengan ResultSet tipe scrollable
        Statement perintah = koneksi.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet dataset = perintah.executeQuery(SQL);

        // Hitung jumlah baris data
        dataset.last(); 
        int baris = dataset.getRow(); 
        dataset.beforeFirst();

        // Inisialisasi array data
        data = new Object[baris][jumlah];

        int j = 0;
        while (dataset.next()) {
            for (int i = 0; i < jumlah; i++) {
                data[j][i] = dataset.getObject(i + 1); // Ambil data berdasarkan indeks
            }
            j++;
        }

        dataset.close();
        perintah.close();
        koneksi.close(); // Tutup koneksi
    } catch (SQLException e) {
        System.err.println("Error isiTabel: " + e.toString());
    }

    return data;
}

   public void tampilTabel(javax.swing.JTable tabel, String SQL, String[] judulKolom) {
    try {
        // Mengambil data dari database
        Object[][] data = isiTabel(SQL, judulKolom.length);

        // Membuat model tabel
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, judulKolom);

        // Mengatur model ke JTable
        tabel.setModel(model);
    } catch (Exception e) {
        System.err.println("Error tampilTabel: " + e.toString());
    }
}
   public void tampilLaporan(String laporanFile, String SQL) {
    try {
        File file = new File(laporanFile);
        JasperDesign jasDes = JRXmlLoader.load(file);

        JRDesignQuery sqlQuery = new JRDesignQuery();
        sqlQuery.setText(SQL);
        jasDes.setQuery(sqlQuery);

        JasperReport JR = JasperCompileManager.compileReport(jasDes);
        JasperPrint JP = JasperFillManager.fillReport(JR, null, getKoneksiDB());
        JasperViewer.viewReport(JP);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.toString());
    }
}     
}