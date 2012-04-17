package cz.payola.domain.rdf

import cz.payola.common.rdf.Edge

class RDFEdge(protected val _origin: RDFIdentifiedNode, protected val _destination: RDFNode,
    protected val _uri: String) extends Edge
{
    type VertexType = RDFNode

    type IdentifiedVertexType = RDFIdentifiedNode
}