package com.vasberc.data_remote.response_model


import com.google.gson.annotations.SerializedName

data class GetMovieResponse(
    @SerializedName("adult")
    val adult: Boolean?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("belongs_to_collection")
    val belongsToCollection: Any?,
    @SerializedName("budget")
    val budget: Int?,
    @SerializedName("credits")
    val credits: Credits?,
    @SerializedName("genres")
    val genres: List<Genre?>?,
    @SerializedName("homepage")
    val homepage: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("imdb_id")
    val imdbId: String?,
    @SerializedName("original_language")
    val originalLanguage: String?,
    @SerializedName("original_title")
    val originalTitle: String?,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("popularity")
    val popularity: Double?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany?>?,
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry?>?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("revenue")
    val revenue: Int?,
    @SerializedName("runtime")
    val runtime: Int?,
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage?>?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("tagline")
    val tagline: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("video")
    val video: Boolean?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?
) {
    data class Credits(
        @SerializedName("cast")
        val cast: List<Cast?>?,
        @SerializedName("crew")
        val crew: List<Crew?>?
    ) {
        data class Cast(
            @SerializedName("adult")
            val adult: Boolean?,
            @SerializedName("cast_id")
            val castId: Int?,
            @SerializedName("character")
            val character: String?,
            @SerializedName("credit_id")
            val creditId: String?,
            @SerializedName("gender")
            val gender: Int?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("known_for_department")
            val knownForDepartment: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("order")
            val order: Int?,
            @SerializedName("original_name")
            val originalName: String?,
            @SerializedName("popularity")
            val popularity: Double?,
            @SerializedName("profile_path")
            val profilePath: String?
        )

        data class Crew(
            @SerializedName("adult")
            val adult: Boolean?,
            @SerializedName("credit_id")
            val creditId: String?,
            @SerializedName("department")
            val department: String?,
            @SerializedName("gender")
            val gender: Int?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("job")
            val job: String?,
            @SerializedName("known_for_department")
            val knownForDepartment: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("original_name")
            val originalName: String?,
            @SerializedName("popularity")
            val popularity: Double?,
            @SerializedName("profile_path")
            val profilePath: String?
        )
    }

    data class Genre(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
    )

    data class ProductionCompany(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("logo_path")
        val logoPath: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("origin_country")
        val originCountry: String?
    )

    data class ProductionCountry(
        @SerializedName("iso_3166_1")
        val iso31661: String?,
        @SerializedName("name")
        val name: String?
    )

    data class SpokenLanguage(
        @SerializedName("english_name")
        val englishName: String?,
        @SerializedName("iso_639_1")
        val iso6391: String?,
        @SerializedName("name")
        val name: String?
    )
}