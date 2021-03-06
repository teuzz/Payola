package cz.payola.domain.entities.analyses.optimization.plugins

import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.rdf.Graph

/**
  * A plugin that during optimization replaces a data fetcher followed by a SPARQL query.
  */
object FetcherQueryPlugin extends Plugin("Multiple merged SPARQL query parts", 0, Nil)
{
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        instance match {
            case dataFetcherWithQuery: FetcherQueryPluginInstance => {
                val sparqlQuery = dataFetcherWithQuery.sparqlQuery
                val query = sparqlQuery.plugin.getQuery(sparqlQuery.instance)
                val dataFetcher = dataFetcherWithQuery.dataFetcher
                dataFetcher.plugin.evaluateWithQuery(dataFetcher.instance, query, progressReporter)
            }
            case _ => throw new PluginException("The specified plugin instance doesn't correspond to the plugin.")
        }
    }
}
