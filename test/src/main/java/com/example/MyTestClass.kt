package com.example

import java.nio.charset.Charset

fun main(args: Array<String>) {
    val text = """+cao tuổi, không ảnh hưởng tuần hoàn não + phản xạ áp lực
+ĐTĐ: không ảnh hưởng chuyển hóa lipid, glucid. Dùng insulin làm K+ vào trong tb hạ K+ máu, UCMC làm giảm aldosteron => giữ K+ máu
+bệnh thận: giảm angiotensin 2 , tăng lưu lượng máu qua thận, giảm renin"""
    println(text.length)
    println(decode_len(text,20))
}

fun decode_len(text: String, org_offset: Int): Any? {
    val bytes = text.toByteArray(Charset.forName("utf-8"))
    println("bytes len: ${bytes.size}")
    var i = 0
    var l = 0
    while (i < bytes.size && i < org_offset) {
        val x = (bytes[i] + 256) % 256
        when {
            x > 0b11110000 -> i += 4
            x > 0b11100000 -> i += 3
            x > 0b11000000 -> i += 2
            else -> i += 1
        }
        l++
    }
    return l
}

