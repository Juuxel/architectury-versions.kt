package juuxel.toolchainversions.maven

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.sequence
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import io.ktor.http.userAgent
import juuxel.toolchainversions.USER_AGENT
import juuxel.toolchainversions.util.Version
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory

class MavenRepository(url: String) {
    private val url: String = url.removeSuffix("/")

    suspend fun getVersions(
        client: HttpClient,
        group: String,
        artifact: String
    ): Either<String, List<Version>> = either {
        val mavenMetadataUrl = "$url/${group.replace('.', '/')}/$artifact/maven-metadata.xml"
        val response = client.get(mavenMetadataUrl) { userAgent(USER_AGENT) }

        if (!response.status.isSuccess()) {
            "Unsuccessful response: ${response.status}".left().bind()
        }

        val body: ByteArray = response.body()
        val bodyIn = ByteArrayInputStream(body)

        @Suppress("BlockingMethodInNonBlockingContext") // ByteArrayInputStream doesn't block lol
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bodyIn)
        val mainNode = document.getElementsByTagName("versioning").single().bind() as Element
        mainNode.getElementByTagName("versions").bind()
            .getElementsByTagName("version")
            .asSequence()
            .map { Version.parse(it.textContent) }
            .sequence()
            .bind()
    }.mapLeft { "Error fetching $group:$artifact versions from repository $url: $it" }
}
