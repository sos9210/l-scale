package reehi.board.article.service.request

class ArticleCreateRequest (
    val title:String,
    val content:String,
    val writerId:Long,
    val boardId:Long,
)