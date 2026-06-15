package com.vietanh.dentalclinic.data

import android.util.Log
import java.sql.Connection
import java.sql.DriverManager

object DatabaseConnection {
    // Lưu ý: IP 10.0.2.2 là localhost của máy tính khi chạy trên máy ảo Android (Emulator)
    // Nếu chạy trên máy thật, cần nhập IP LAN của máy tính chứa SQL Server (VD: 192.168.1.x)
    private const val IP = "10.0.2.2"
    private const val PORT = "1433"
    private const val DB_NAME = "DentalClinicDB"
    private const val USER = "sa"
    private const val PASS = "123"

    /**
     * Hàm mở kết nối tới SQL Server
     * QUAN TRỌNG: Phải gọi hàm này trong một Background Thread (ví dụ: Coroutine Dispatchers.IO),
     * KHÔNG ĐƯỢC gọi trực tiếp trên Main/UI Thread nếu không ứng dụng sẽ bị crash (NetworkOnMainThreadException).
     */
    fun getConnection(): Connection? {
        var connection: Connection? = null
        try {
            // Đăng ký Driver của jTDS (Tương thích tốt hơn với Android)
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            
            // Chuỗi kết nối của jTDS
            val url = "jdbc:jtds:sqlserver://$IP:$PORT/$DB_NAME"
            
            connection = DriverManager.getConnection(url, USER, PASS)
            Log.d("DB_CONN", "Kết nối CSDL thành công!")
        } catch (e: Exception) {
            Log.e("DB_CONN", "Lỗi kết nối CSDL: ${e.message}", e)
        }
        return connection
    }
}
