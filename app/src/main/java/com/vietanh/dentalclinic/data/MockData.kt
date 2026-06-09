package com.vietanh.dentalclinic.data

data class Doctor(val id: Int, val name: String, val specialty: String, val imageUrl: String)
data class Service(val id: Int, val name: String, val description: String, val iconRes: Int? = null)
data class Appointment(val id: Int, val doctorName: String, val serviceName: String, val date: String, val time: String, val status: AppointmentStatus)

enum class AppointmentStatus { PENDING, CONFIRMED, CANCELLED }

object MockData {
    val doctors = listOf(
        Doctor(1, "Dr. Nguyễn Văn A", "Chuyên khoa Chỉnh nha", ""),
        Doctor(2, "Dr. Trần Thị B", "Chuyên khoa Implant", ""),
        Doctor(3, "Dr. Lê Văn C", "Chuyên khoa Răng sứ", "")
    )

    val services = listOf(
        Service(1, "Cạo vôi răng", "Làm sạch mảng bám, vôi răng an toàn"),
        Service(2, "Trám răng thẩm mỹ", "Trám lỗ sâu, phục hồi hình dáng răng"),
        Service(3, "Niềng răng", "Chỉnh nha mắc cài, khay trong suốt"),
        Service(4, "Cấy ghép Implant", "Phục hình răng đã mất hiệu quả vĩnh viễn")
    )

    val appointments = listOf(
        Appointment(1, "Dr. Nguyễn Văn A", "Niềng răng", "25/10/2023", "09:00", AppointmentStatus.CONFIRMED),
        Appointment(2, "Dr. Trần Thị B", "Cạo vôi răng", "28/10/2023", "14:30", AppointmentStatus.PENDING),
        Appointment(3, "Dr. Lê Văn C", "Trám răng thẩm mỹ", "02/11/2023", "10:00", AppointmentStatus.CANCELLED)
    )
}