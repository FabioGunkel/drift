package com.gunkel.android.affectus.theme

import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.core.graphics.createBitmap

object MarkerUtils {
    /**
     * Creates a Situationist "Survey Point" marker.
     * An abstract diamond shape with a central focus point, 
     * representing an emotional node in the urban drift.
     */
    fun createMarker(color: Color, isKeyPoint: Boolean = false): BitmapDescriptor {
        val size = if (isKeyPoint) 110 else 80
        val bitmap = createBitmap(size, size)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Center coordinates
        val cx = size / 2f
        val cy = size / 2f
        val radius = size / 2.5f

        // Draw shadow (soft glow)
        paint.color = android.graphics.Color.BLACK
        paint.alpha = 30
        canvas.drawCircle(cx, cy + 4, radius + 2, paint)

        // Draw Diamond Path
        val path = Path()
        path.moveTo(cx, 0f) // Top
        path.lineTo(size.toFloat(), cy) // Right
        path.lineTo(cx, size.toFloat()) // Bottom
        path.lineTo(0f, cy) // Left
        path.close()

        // Fill with brand color
        paint.style = Paint.Style.FILL
        paint.color = color.toArgb()
        paint.alpha = 255
        canvas.drawPath(path, paint)

        // Draw white border for contrast
        paint.style = Paint.Style.STROKE
        paint.color = android.graphics.Color.WHITE
        paint.strokeWidth = if (isKeyPoint) 8f else 5f
        canvas.drawPath(path, paint)

        // Draw central eye (observation point)
        paint.style = Paint.Style.FILL
        paint.color = android.graphics.Color.WHITE
        canvas.drawCircle(cx, cy, if (isKeyPoint) size / 10f else size / 12f, paint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
