package reehi.board.articleread.cache

@FunctionalInterface
interface OptimizedCacheOriginDataSupplier<T>  {
    fun get() :T
}