package com.ngxqt.mdm.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ngxqt.mdm.data.model.Equipment
import com.ngxqt.mdm.data.remote.ApiInterface
import retrofit2.HttpException
import java.io.IOException



class EquipmentsPagingSource(
    private val mdmApi: ApiInterface,
    private val authorization: String,
    private val status: String?,
    private val keyword: String?,
    private val departmenId: Int?
) : PagingSource<Int,Equipment>() {
    companion object {
        private const val STARTING_INDEX = 1
        private const val PER_PAGE = 10
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Equipment> {
        val page = params.key ?: STARTING_INDEX

        return try {
            val response = mdmApi.getEquipments(authorization, page, PER_PAGE, status, keyword, departmenId)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val data = body.data.data
                    LoadResult.Page(
                        data = data,
                        prevKey = if (page == STARTING_INDEX) null else page - 1,
                        nextKey = if (data.isEmpty()) null else page + 1
                    )
                } ?: run {
                    LoadResult.Error(NullPointerException("Response body is null"))
                }
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Equipment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}