package reehi.board.article.service.response

class ArticlePageResponse (
    val articles :List<ArticleResponse>,
    val articleCount :Long,
){
    companion object{
        fun of(articles: List<ArticleResponse>, articleCount: Long) =
            ArticlePageResponse(articles, articleCount)
    }
}