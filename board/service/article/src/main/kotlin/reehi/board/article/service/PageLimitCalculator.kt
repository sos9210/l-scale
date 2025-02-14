package reehi.board.article.service

class PageLimitCalculator {

    companion object {
        fun calculatePageLimit(page: Long, pageSize: Long, moveablePageCount: Long): Long =
            (((page - 1) / moveablePageCount) + 1) * pageSize * moveablePageCount + 1
    }
}