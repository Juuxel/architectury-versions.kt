package juuxel.toolchainversions.maven

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

@Suppress("UNCHECKED_CAST")
fun Element.getElementByTagName(name: String): Either<String, Element> =
    getElementsByTagName(name).single() as Either<String, Element>

fun NodeList.single(): Either<String, Node> {
    if (length != 1) {
        return "Expected single element, found $length elements".left()
    }

    return item(0).right()
}

operator fun NodeList.iterator(): Iterator<Node> = iterator {
    for (i in 0 until length) {
        yield(item(i))
    }
}

fun NodeList.asSequence(): Sequence<Node> = Sequence { iterator() }
