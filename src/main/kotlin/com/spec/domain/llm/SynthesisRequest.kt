package com.spec.domain.llm

data class SynthesisSchema(
    val fields: List<String>
) {
    init {
        require(fields.isNotEmpty()) { "Schema must have at latest one field" }
    }
}

data class SynthesisRequest(
    val rawText: String,
    val schema: SynthesisSchema
) {
    init {
        require(rawText.isNotBlank()) { "Raw text must not be blank" }
    }
}
