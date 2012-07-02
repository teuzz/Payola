package cz.payola.web.shared

import cz.payola.common.rdf._
import cz.payola.domain.rdf._
import cz.payola.domain.rdf.LiteralVertex
import cz.payola.common.rdf.Graph

@remote object GraphFetcher
{
    def getInitialGraph: Graph = {
        //(new DataFacade).getGraph("http://dbpedia.org/resource/Prague")
        initGraph()
    }

    def getNeighborhoodOfVertex(vertexUri: String): Graph = {
        //(new DataFacade).getGraph(vertexUri)
        //^this is just a quick fix, dunno if it is correct, TODO implement it via calling model.
        initGraph()
    }

    def initGraph(): Graph = {

        val v0  = new IdentifiedVertex("0")
        val v1  = new IdentifiedVertex("1")
        val v2  = new IdentifiedVertex("2")
        val v3  = new IdentifiedVertex("3")
        val v4  = new IdentifiedVertex("4")
        val v5  = new IdentifiedVertex("5")
        val v6  = new IdentifiedVertex("6")
        val v7  = new IdentifiedVertex("7")
        val v8  = new IdentifiedVertex("8")
        val v9  = new IdentifiedVertex("9")
        val v10 = new IdentifiedVertex("10")
        val v11 = new IdentifiedVertex("11")
        val v12 = new IdentifiedVertex("12")
        val v13 = new IdentifiedVertex("13")
        val v14 = new IdentifiedVertex("14")
        val v15 = new IdentifiedVertex("15")
        val v16 = new IdentifiedVertex("16")
        val v17 = new IdentifiedVertex("17")
        val v18 = new IdentifiedVertex("18")
        val v19 = new IdentifiedVertex("19")
        val v20 = new IdentifiedVertex("20")
        val v21 = new IdentifiedVertex("21")
        val v22 = new IdentifiedVertex("22")
        val v23 = new IdentifiedVertex("23")
        val v24 = new IdentifiedVertex("24")
        val v25 = new IdentifiedVertex("25")
        val v26 = new IdentifiedVertex("26")
        val v27 = new IdentifiedVertex("27")
        val v28 = new IdentifiedVertex("28")
        val v29 = new IdentifiedVertex("29")
        val v30 = new IdentifiedVertex("30")
        val v31 = new IdentifiedVertex("31")
        val v32 = new IdentifiedVertex("32")
        val v33 = new IdentifiedVertex("33")
        val v34 = new IdentifiedVertex("34")
        val v35 = new IdentifiedVertex("35")
        val v36 = new IdentifiedVertex("36")
        val v37 = new IdentifiedVertex("37")
        val v38 = new IdentifiedVertex("38")
        val v39 = new IdentifiedVertex("39")
        val v40 = new IdentifiedVertex("40")
        val v41 = new IdentifiedVertex("41")
        val v42 = new IdentifiedVertex("42")
        val v43 = new IdentifiedVertex("43")
        val v44 = new IdentifiedVertex("44")
        val v45 = new IdentifiedVertex("45")
        val v46 = new IdentifiedVertex("46")
        val v47 = new IdentifiedVertex("47")
        val v48 = new IdentifiedVertex("48")
        val v49 = new IdentifiedVertex("49")
        val v50 = new LiteralVertex("猿", None)
        val v51 = new LiteralVertex("カタカナ", None)
        val v52 = new LiteralVertex("漢字", None)
        val v53 = new LiteralVertex("ひらがな", None)
        val v54 = new LiteralVertex("がっこう", None)
        val v55 = new LiteralVertex("プラハ", None)
        val v56 = new LiteralVertex("トースト", None)
        val v57 = new LiteralVertex("高い家具", None)
        val v58 = new LiteralVertex("丁寧語", None)
        val v59 = new LiteralVertex("わたし", None)
        val v60 = new LiteralVertex("東京", None)
        val v61 = new LiteralVertex("京", None)
        val v62 = new LiteralVertex("酒は米から作る", None)
        val v63 = new LiteralVertex("静かに", None)
        val v64 = new LiteralVertex("海の魚", None)
        val v65 = new LiteralVertex("雨の音", None)
        val v66 = new LiteralVertex("luňák", None)
        val v67 = new LiteralVertex("ťuk", None)
        val v68 = new LiteralVertex("řeřicha", None)
        val v69 = new LiteralVertex("pingů", None)
        val v70 = new LiteralVertex("北海道", None)
        val v71 = new LiteralVertex("となりのととろ", None)
        val v72 = new LiteralVertex("sportovní pes", None)



        val e1 = new Edge(v0, v1, "0")
        val e2 = new Edge(v0, v2, "1")
        val e3 = new Edge(v0, v3, "2")
        val e4 = new Edge(v0, v4, "3")
        val e5 = new Edge(v0, v5, "4")
        val e6 = new Edge(v0, v6, "5")
        val e7 = new Edge(v0, v7, "6")
        val e8 = new Edge(v0, v8, "7")
        val e9 = new Edge(v0, v50, "0")
        val e10 = new Edge(v0, v71, "8")
        val e11 = new Edge(v1, v8, "3")
        val e12 = new Edge(v1, v9, "4")
        val e13 = new Edge(v1, v10, "3")
        val e14 = new Edge(v4, v13, "5")
        val e15 = new Edge(v4, v14, "7")
        val e16 = new Edge(v4, v15, "1")
        val e17 = new Edge(v4, v16, "0")
        val e18 = new Edge(v4, v17, "2")
        val e19 = new Edge(v4, v18, "4")
        val e20 = new Edge(v4, v19, "8")
        val e21 = new Edge(v4, v20, "1")
        val e22 = new Edge(v5, v10, "1")
        val e23 = new Edge(v5, v12, "2")
        val e24 = new Edge(v5, v13, "3")
        val e25 = new Edge(v5, v20, "4")
        val e26 = new Edge(v5, v51, "0")
        val e27 = new Edge(v6, v8, "5")
        val e28 = new Edge(v6, v9, "4")
        val e29 = new Edge(v6, v10, "1")
        val e30 = new Edge(v7, v20, "2")
        val e31 = new Edge(v7, v21, "3")
        val e32 = new Edge(v7, v22, "1")
        val e33 = new Edge(v7, v23, "4")
        val e34 = new Edge(v7, v24, "5")
        val e35 = new Edge(v7, v25, "6")
        val e36 = new Edge(v7, v72, "7")
        val e37 = new Edge(v8, v10, "1")
        val e38 = new Edge(v8, v15, "4")
        val e39 = new Edge(v8, v52, "0")
        val e40 = new Edge(v9, v10, "5")
        val e41 = new Edge(v9, v25, "6")
        val e42 = new Edge(v9, v26, "0")
        val e43 = new Edge(v9, v53, "0")
        val e44 = new Edge(v9, v54, "0")
        val e45 = new Edge(v9, v55, "0")
        val e46 = new Edge(v10, v25, "0")
        val e47 = new Edge(v10, v26, "0")
        val e48 = new Edge(v10, v27, "0")
        val e49 = new Edge(v10, v28, "0")
        val e50 = new Edge(v10, v29, "0")
        val e51 = new Edge(v10, v30, "0")
        val e52 = new Edge(v11, v15, "0")
        val e53 = new Edge(v11, v16, "0")
        val e54 = new Edge(v11, v18, "0")
        val e55 = new Edge(v11, v30, "0")
        val e56 = new Edge(v11, v56, "0")
        val e57 = new Edge(v12, v15, "0")
        val e58 = new Edge(v12, v16, "0")
        val e59 = new Edge(v13, v30, "0")
        val e60 = new Edge(v14, v31, "0")
        val e61 = new Edge(v15, v30, "0")
        val e62 = new Edge(v15, v31, "0")
        val e63 = new Edge(v15, v32, "0")
        val e64 = new Edge(v15, v33, "0")
        val e65 = new Edge(v15, v34, "0")
        val e66 = new Edge(v15, v57, "0")
        val e67 = new Edge(v16, v35, "0")
        val e68 = new Edge(v16, v36, "0")
        val e69 = new Edge(v16, v37, "0")
        val e70 = new Edge(v16, v38, "0")
        val e71 = new Edge(v16, v39, "0")
        val e72 = new Edge(v16, v40, "0")
        val e73 = new Edge(v17, v38, "0")
        val e74 = new Edge(v17, v39, "0")
        val e75 = new Edge(v17, v40, "0")
        val e76 = new Edge(v20, v58, "0")
        val e77 = new Edge(v24, v59, "0")
        val e78 = new Edge(v25, v60, "0")
        val e79 = new Edge(v25, v61, "0")
        val e80 = new Edge(v29, v62, "0")
        val e81 = new Edge(v30, v40, "0")
        val e82 = new Edge(v30, v41, "0")
        val e83 = new Edge(v30, v42, "0")
        val e84 = new Edge(v30, v43, "0")
        val e85 = new Edge(v30, v63, "0")
        val e86 = new Edge(v34, v64, "0")
        val e87 = new Edge(v34, v65, "0")
        val e88 = new Edge(v34, v66, "0")
        val e89 = new Edge(v35, v44, "0")
        val e90 = new Edge(v35, v45, "0")
        val e91 = new Edge(v35, v46, "0")
        val e92 = new Edge(v35, v47, "0")
        val e93 = new Edge(v35, v48, "0")
        val e94 = new Edge(v35, v49, "0")
        val e95 = new Edge(v38, v67, "0")
        val e96 = new Edge(v40, v68, "0")
        val e97 = new Edge(v42, v69, "0")
        val e98 = new Edge(v44, v70, "0")
        val e99 = new Edge(v44, v71, "0")





        new cz.payola.domain.rdf.Graph(
            List(
                v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20,
                v21, v22, v23, v24, v25, v26, v27, v28, v29, v30, v31, v32, v33, v34, v35, v36, v37, v38, v39, v40,
                v41, v42, v43, v44, v45, v46, v47, v48, v49, v50, v51, v52, v53, v54, v55, v56, v57, v58, v59, v60,
                v61, v62, v63, v64, v65, v66, v67, v68, v69, v70, v71, v72
            ),
            List(
                e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17, e18, e19, e20,
                e21, e22, e23, e24, e25, e26, e27, e28, e29, e30, e31, e32, e33, e34, e35, e36, e37, e38, e39, e40,
                e41, e42, e43, e44, e45, e46, e47, e48, e49, e50, e51, e52, e53, e54, e55, e56, e57, e58, e59, e60,
                e61, e62, e63, e64, e65, e66, e67, e68, e69, e70, e71, e72, e73, e74, e75, e76, e77, e78, e79, e80,
                e81, e82, e83, e84, e85, e86, e87, e88, e89, e90, e91, e92, e93, e94, e95, e96, e97, e98, e99
            )
        )
    }
}
