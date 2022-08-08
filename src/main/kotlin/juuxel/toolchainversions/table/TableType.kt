package juuxel.toolchainversions.table

import arrow.core.Either
import io.ktor.client.HttpClient

interface TableType {
    suspend fun createTable(client: HttpClient, gameVersion: String?): Either<String, Table>
}
