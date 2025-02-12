package vn.dihaver.tech.bank.widget.utils

import android.content.Context
import vn.dihaver.tech.bank.widget.R
import vn.dihaver.tech.bank.widget.data.model.BodyShape
import vn.dihaver.tech.bank.widget.data.model.EyeBallShape
import vn.dihaver.tech.bank.widget.data.model.EyeFrameShape
import vn.dihaver.tech.bank.widget.data.model.ShapeQrEntity
import vn.dihaver.tech.bank.widget.data.model.ThemeEntity
import java.io.File

object CustomizationList {
    /** Danh sách màu Foreground */
    val colorFores = listOf(
        "#FF000000", "#FFC40002", "#FFC04301", "#FFC87C02", "#FF00B30A",
        "#FF00ACB6", "#FF0039C8", "#FF6400D7", "#FFB100C0", "#FF8E019B",
        "#FF002B9B", "#FF029199", "#FF009909", "#FF998002", "#FF996102",
        "#FF993503", "#FF990100"
    )

    /** Danh sách màu Background */
    val colorBacks = listOf(
        "#FFFFFFFF", "#FFFEFDF9", "#FFFFF7F8", "#FFFEF5EE", "#FFF7FCF6", "#FFF3FDFF"
    )

    /** Danh sách chủ đề */
    val themes = listOf(
        ThemeEntity("bg_not_have", "Không có", R.drawable.bg_not_have),
        ThemeEntity("bg_plain_yellow", "Vàng trơn", R.drawable.bg_plain_yellow),
        ThemeEntity("bg_plain_green", "Xanh lục trơn", R.drawable.bg_plain_green),
        ThemeEntity("bg_plain_blue", "Xanh dương trơn", R.drawable.bg_plain_blue),
        ThemeEntity("bg_plain_pink", "Hồng trơn", R.drawable.bg_plain_pink),
        ThemeEntity("bg_plain_brown", "Nâu trơn", R.drawable.bg_plain_brown),

        ThemeEntity("bg_rect_line_yellow", "Vàng đường thẳng", R.drawable.bg_rect_line_yellow),
        ThemeEntity("bg_rect_line_green", "Xanh lục đường thẳng", R.drawable.bg_rect_line_green),
        ThemeEntity("bg_rect_line_blue", "Xanh dương đường thẳng", R.drawable.bg_rect_line_blue),
        ThemeEntity("bg_rect_line_pink", "Hồng đường thẳng", R.drawable.bg_rect_line_pink),
        ThemeEntity("bg_rect_line_brown", "Nâu đường thẳng", R.drawable.bg_rect_line_brown),

        ThemeEntity(
            "bg_contour_line_yellow",
            "Vàng đường đồng mức",
            R.drawable.bg_contour_line_yellow
        ),
        ThemeEntity(
            "bg_contour_line_green",
            "Xanh lục đường đồng mức",
            R.drawable.bg_contour_line_green
        ),
        ThemeEntity(
            "bg_contour_line_blue",
            "Xanh dương đường đồng mức",
            R.drawable.bg_contour_line_blue
        ),
        ThemeEntity("bg_contour_line_pink", "Hồng đường đồng mức", R.drawable.bg_contour_line_pink),
        ThemeEntity(
            "bg_contour_line_brown",
            "Nâu đường đồng mức",
            R.drawable.bg_contour_line_brown
        ),

        ThemeEntity("bg_hexagon_yellow", "Vàng lục giác", R.drawable.bg_hexagon_yellow),
        ThemeEntity("bg_hexagon_green", "Xanh lục lục giác", R.drawable.bg_hexagon_green),
        ThemeEntity("bg_hexagon_blue", "Xanh dương lục giác", R.drawable.bg_hexagon_blue),
        ThemeEntity("bg_hexagon_pink", "Hồng lục giác", R.drawable.bg_hexagon_pink),
        ThemeEntity("bg_hexagon_brown", "Nâu lục giác", R.drawable.bg_hexagon_brown)
    )

    /** Danh sách Logo */
    fun createImageList(context: Context, bankIconRes: String): List<String> {
        val imageList = mutableListOf<String>()
        imageList.add("null")
        imageList.add(BitmapUtils.convertNameToPath(bankIconRes, BitmapUtils.PathType.RES))
        val imageUserDir = File(context.getExternalFilesDir(null), "user-image")
        if (imageUserDir.exists() && imageUserDir.isDirectory) {
            val pngFiles = imageUserDir.listFiles { file ->
                file.isFile && file.extension.equals("png", ignoreCase = true)
            }
            pngFiles?.sortedByDescending { it.lastModified() }?.forEach { file ->
                imageList.add(file.absolutePath)
            }
        }
        imageList.add("add")
        return imageList
    }

    /** Danh sách hình dạng thân */
    val bodyShapes = listOf(
        ShapeQrEntity(
            bodyShape = BodyShape.Default,
            resPreview = R.drawable.svg_preview_body_default
        ),
        ShapeQrEntity(bodyShape = BodyShape.Rect, resPreview = R.drawable.svg_preview_body_rect),
        ShapeQrEntity(
            bodyShape = BodyShape.Circle,
            resPreview = R.drawable.svg_preview_body_circle
        ),
        ShapeQrEntity(
            bodyShape = BodyShape.CircleMini,
            resPreview = R.drawable.svg_preview_body_circle_mini
        ),
        ShapeQrEntity(
            bodyShape = BodyShape.Rhombus,
            resPreview = R.drawable.svg_preview_body_rhombus
        ),
        ShapeQrEntity(
            bodyShape = BodyShape.RoundCorners,
            resPreview = R.drawable.svg_preview_body_round_corners
        ),
        ShapeQrEntity(
            bodyShape = BodyShape.RoundCornersHorizontal,
            resPreview = R.drawable.svg_preview_body_round_corners_horizontal
        ),
        ShapeQrEntity(
            bodyShape = BodyShape.RoundCornersVertical,
            resPreview = R.drawable.svg_preview_body_round_corners_vertical
        ),
        ShapeQrEntity(bodyShape = BodyShape.Star, resPreview = R.drawable.svg_preview_body_star)
    )

    val eyeFrameShapes = listOf(
        ShapeQrEntity(
            eyeFrameShape = EyeFrameShape.Default,
            resPreview = R.drawable.svg_preview_eye_frame_default
        ),
        ShapeQrEntity(
            eyeFrameShape = EyeFrameShape.Circle,
            resPreview = R.drawable.svg_preview_eye_frame_circle
        ),
        ShapeQrEntity(
            eyeFrameShape = EyeFrameShape.RoundCorners,
            resPreview = R.drawable.svg_preview_eye_frame_round_corners
        ),
        ShapeQrEntity(
            eyeFrameShape = EyeFrameShape.RoundCornersR,
            resPreview = R.drawable.svg_preview_eye_frame_round_corners_r
        ),
        ShapeQrEntity(
            eyeFrameShape = EyeFrameShape.RoundCornersZ,
            resPreview = R.drawable.svg_preview_eye_frame_round_corners_z
        )
    )

    val eyeBallShapes = listOf(
        ShapeQrEntity(
            eyeBallShape = EyeBallShape.Default,
            resPreview = R.drawable.svg_preview_eye_ball_default
        ),
        ShapeQrEntity(
            eyeBallShape = EyeBallShape.Circle,
            resPreview = R.drawable.svg_preview_eye_ball_circle
        ),
        ShapeQrEntity(
            eyeBallShape = EyeBallShape.RoundCorners,
            resPreview = R.drawable.svg_preview_eye_ball_round_corners
        ),
        ShapeQrEntity(
            eyeBallShape = EyeBallShape.Rect,
            resPreview = R.drawable.svg_preview_eye_ball_rect
        ),
        ShapeQrEntity(
            eyeBallShape = EyeBallShape.RoundCornersHorizontal,
            resPreview = R.drawable.svg_preview_eye_ball_round_corners_horizontal
        ),
        ShapeQrEntity(
            eyeBallShape = EyeBallShape.RoundCornersVertical,
            resPreview = R.drawable.svg_preview_eye_ball_round_corners_vertical
        )
    )
}
