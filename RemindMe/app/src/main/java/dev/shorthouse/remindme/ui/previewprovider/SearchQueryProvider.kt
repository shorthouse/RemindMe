package dev.shorthouse.remindme.ui.previewprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class SearchQueryProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "",
        "Water the plants"
    )
}
