package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.model._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.entities.plugins.parameters.StringParameterValue
import scala.collection.mutable.HashMap
import cz.payola.domain.entities.analyses.evaluation._
import cz.payola.domain.IDGenerator
import cz.payola.common._
import cz.payola.domain.entities.plugins.concrete.data.SparqlEndpointFetcher
import cz.payola.domain.entities.plugins.concrete.query._
import scala.collection.mutable
import cz.payola.common.entities.analyses.PluginInstanceBinding
import scala.Some
import cz.payola.common.EvaluationError
import cz.payola.domain.entities.analyses.evaluation.Success
import cz.payola.common.EvaluationInProgress
import cz.payola.domain.entities.analyses.evaluation.Error
import cz.payola.common.EvaluationSuccess

trait AnalysisModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PrivilegeModelComponent =>
    val runningEvaluations: HashMap[String, (Option[User], AnalysisEvaluation, Long)] = new
            HashMap[String, (Option[User], AnalysisEvaluation, Long)]

    lazy val analysisModel = new ShareableEntityModel(analysisRepository, classOf[Analysis])
    {
        def addBinding(analysisId: String, sourceId: String, targetId: String, inputIndex: Int) {
            getById(analysisId).map {
                a =>
                    val source = a.pluginInstances.find(_.id == sourceId)
                    val target = a.pluginInstances.find(_.id == targetId)

                    if (!source.isDefined || !target.isDefined) {
                        throw new Exception("Invalid source or target.")
                    }

                    a.addBinding(source.get, target.get, inputIndex)
            }.getOrElse {
                throw new Exception("Unknown analysis.")
            }
        }

        private def clonePluginInstances(original: Seq[PluginInstance], bindings: Seq[PluginInstanceBinding],
            targetAnalysis: Analysis) : HashMap[String, String] = {
            val translateMap = HashMap[String, String]()

            original.map {
                p =>
                    val instance = createPluginInstance(p.plugin.id, targetAnalysis.id)
                    translateMap.put(p.id, instance.id)

                    analysisRepository.getById(targetAnalysis.id).map {
                        a =>
                            a.pluginInstances.find(_.id == instance.id).map {
                                pi =>
                                    p.parameterValues.map {
                                        v =>
                                            setParameterValue(pi, v.parameter.name, v.value.toString)
                                    }
                            }
                    }
            }

            bindings.map {
                b =>
                    addBinding(targetAnalysis.id, translateMap.get(b.sourcePluginInstance.id).get,
                        translateMap.get(b.targetPluginInstance.id).get, b.targetInputIndex)
            }

            translateMap
        }

        def cloneAndEdit(analysisId: String, newOwner: User): Analysis = {
            getAccessibleToUser(Some(newOwner)).find(_.id == analysisId).map {
                a =>
                    val newAnalysis = new Analysis(a.name + IDGenerator.newId, Some(newOwner))
                    persist(newAnalysis)

                    clonePluginInstances(a.pluginInstances, a.pluginInstanceBindings, newAnalysis)

                    newAnalysis
            }.getOrElse {
                throw new ModelException("Unknown analysis ID.")
            }
        }

        def create(owner: User, name: String): Analysis = {
            val analysis = new Analysis(name, Some(owner))
            persist(analysis)
            analysis
        }

        def createPluginInstance(pluginId: String, analysisId: String): PluginInstance = {
            val analysis = analysisRepository.getById(analysisId).getOrElse {
                throw new ModelException("Unknown analysis ID.")
            }

            val instance = pluginRepository.getById(pluginId).map(_.createInstance()).getOrElse {
                throw new ModelException("Unknown plugin ID.")
            }

            analysis.addPluginInstance(instance)
            instance
        }

        def setParameterValue(user: User, analysisId: String, pluginInstanceId: String, parameterName: String,
            value: String) {
            val analysis = user.ownedAnalyses
                .find(_.id == analysisId)
                .get

            val pluginInstance = analysis.pluginInstances.find(_.id == pluginInstanceId)
            pluginInstance.map {
                i =>
                    setParameterValue(i, parameterName, value)
            }.getOrElse {
                throw new ModelException("Unknown plugin instance ID.")
            }
        }

        def setParameterValue(pluginInstance: PluginInstance, parameterName: String, value: String) {
            if (!pluginInstance.isEditable) {
                throw new ModelException("The plugin instance is not editable.")
            }

            val option = pluginInstance.getParameterValue(parameterName)

            if (!option.isDefined) {
                throw new Exception("Unknown parameter name: " + parameterName + ".")
            }

            val parameterValue = option.get

            parameterValue match {
                case v: BooleanParameterValue => v.value = value.toBoolean
                case v: FloatParameterValue => v.value = value.toFloat
                case v: IntParameterValue => v.value = value.toInt
                case v: StringParameterValue => v.value = value
                case _ => throw new Exception("Unknown parameter type.")
            }

            analysisRepository.persistParameterValue(parameterValue)
        }

        def removePluginInstanceById(analysisId: String, pluginInstanceId: String): Boolean = {
            val analysis = analysisRepository.getById(analysisId).getOrElse {
                throw new ModelException("Unknown analysis ID.")
            }

            val instance = analysis.pluginInstances.find(_.id == pluginInstanceId).getOrElse {
                throw new ModelException("Unknown plugin instance ID.")
            }

            analysis.removePluginInstance(instance)
            analysis.pluginInstances.contains(instance)
        }

        def removePluginInstanceBindingById(analysisId: String, pluginInstanceBindingId: String): Boolean = {
            val analysis = analysisRepository.getById(analysisId).getOrElse {
                throw new ModelException("Unknown analysis ID.")
            }

            val binding = analysis.pluginInstanceBindings.find(_.id == pluginInstanceBindingId).getOrElse {
                throw new ModelException("Unknown plugin instance ID.")
            }

            analysis.removeBinding(binding)
            analysis.pluginInstanceBindings.contains(binding)
        }

        def run(analysis: Analysis, timeoutSeconds: Long, oldEvaluationId: String, user: Option[User] = None) = {
            if (runningEvaluations.isDefinedAt(oldEvaluationId)) {
                if (!runningEvaluations.get(oldEvaluationId).filter(_._2.analysis.id == analysis.id).isEmpty) {
                    runningEvaluations.remove(oldEvaluationId)
                }
            }

            val evaluationId = IDGenerator.newId
            val timeout = scala.math.min(1800, timeoutSeconds)
            runningEvaluations
                .put(evaluationId, (user, analysis.evaluate(Some(timeout * 1000)), (new java.util.Date).getTime))

            evaluationId
        }

        private def getEvaluationTupleForID(id: String) = {
            val date = new java.util.Date
            runningEvaluations.foreach {
                tuple =>
                    if (tuple._2._3 + (20 * 60 * 1000) < date.getTime) {
                        runningEvaluations.remove(tuple._1)
                    }
            }

            runningEvaluations.get(id).getOrElse {
                throw new ModelException("The evaluation is not running.")
            }
        }

        def getEvaluationState(evaluationId: String, user: Option[User] = None) = {
            val evaluationTuple = getEvaluationTupleForIDAndPerformSecurityChecks(evaluationId, user)

            runningEvaluations.put(evaluationId, (evaluationTuple._1, evaluationTuple._2, (new java.util.Date).getTime))

            val evaluation = evaluationTuple._2

            evaluation.getResult.map {
                case r: Error => EvaluationError(transformException(r.error),
                    r.instanceErrors.toList.map {
                        e => (e._1, transformException(e._2))
                    })
                case r: Success => EvaluationSuccess(r.outputGraph,
                    r.instanceErrors.toList.map {
                        e => (e._1, transformException(e._2))
                    })
                case Timeout => new EvaluationTimeout
                case _ => throw new Exception("Unhandled evaluation state")
            }.getOrElse {
                val progress = evaluation.getProgress
                EvaluationInProgress(progress.value, progress.evaluatedInstances, progress.runningInstances.toList,
                    progress.errors.toList.map {
                        e => (e._1, transformException(e._2))
                    })
            }
        }

        private def transformException(t: Throwable): String = {
            t match {
                case e: Exception => e.getMessage
                case _ => "Unknown error."
            }
        }

        def getEvaluationTupleForIDAndPerformSecurityChecks(id: String, user: Option[User]) = {
            val evaluationTuple = getEvaluationTupleForID(id)
            if (!evaluationTuple._1.isDefined || evaluationTuple._1 == user) {
                evaluationTuple
            } else {
                throw new ModelException("Forbidden evaluation.")
            }
        }

        def createAnonymousAnalysis(user: Option[User], endpointUri: String, graphUris: List[String],
            classUri: Option[String], propertyUri: Option[String]) = {
            lazy val endpointPluginId = pluginRepository.getByName("SPARQL Endpoint").map(_.id).getOrElse("")
            lazy val typedPluginId = pluginRepository.getByName("Typed").map(_.id).getOrElse("")
            lazy val propertyPluginId = pluginRepository.getByName("Property Selection").map(_.id).getOrElse("")

            val analysis = new Analysis(IDGenerator.newId, user)
            analysis.isPublic = true
            analysis.token = Some(IDGenerator.newId)
            persist(analysis)

            val endpointInstance = createPluginInstance(endpointPluginId, analysis.id)

            val typedInstance = classUri.map {
                u =>
                    val typedInstance = createPluginInstance(typedPluginId, analysis.id)
                    addBinding(analysis.id, endpointInstance.id, typedInstance.id, 0)
                    typedInstance
            }

            val propertyInstance = propertyUri.map {
                u =>
                    val propertyInstance = createPluginInstance(propertyPluginId, analysis.id)
                    val instanceId = typedInstance.getOrElse(endpointInstance).id
                    addBinding(analysis.id, instanceId, propertyInstance.id, 0)
                    propertyInstance
            }

            val persistedAnalysis = analysisRepository.getById(analysis.id)
            persistedAnalysis.map {
                a =>

                    val persistedInstances = a.pluginInstances

                    persistedInstances.find(_.id == endpointInstance.id).map {
                        e =>
                            setParameterValue(e, SparqlEndpointFetcher.endpointURLParameter, endpointUri)
                            setParameterValue(e, SparqlEndpointFetcher.graphURIsParameter, graphUris.mkString("\n"))
                    }

                    typedInstance.map {
                        t =>
                            persistedInstances.find(_.id == t.id)
                                .map(setParameterValue(_, Typed.typeURIParameter, classUri.get))
                    }
                    propertyInstance.map {
                        p =>
                            persistedInstances.find(_.id == p.id)
                                .map(setParameterValue(_, PropertySelection.propertyURIsParameter, propertyUri.get))
                    }
            }

            analysis
        }

        def takeOwnership(analysisId: String, user: User, availableTokens: Seq[String]) {
            getById(analysisId).map {
                a =>
                    val canTakeOwnership = a.token.isDefined && availableTokens.contains(a.token.get)

                    if (canTakeOwnership) {
                        a.owner = Some(user)
                        a.token = None
                        persist(a)
                    }
            }
        }

        def makePartial(analysis: Analysis, pluginInstanceId: String): Option[String] = {
            val lastOutput = analysis.pluginInstanceBindings.find(_.targetPluginInstance.id == pluginInstanceId)

            lastOutput.map {
                o =>
                    val waiting = mutable.Queue(o.sourcePluginInstance)
                    val instances = new mutable.ListBuffer[PluginInstance]
                    val bindings = new mutable.ListBuffer[PluginInstanceBinding]

                    while (!waiting.isEmpty) {
                        val current = waiting.dequeue()
                        instances += current

                        val pre = analysis.pluginInstanceBindings.filter(_.targetPluginInstance == current)
                        pre.map {
                            b =>
                                waiting.enqueue(b.sourcePluginInstance)
                                bindings += b
                        }
                    }

                    val partial = new Analysis(pluginInstanceId, analysis.owner)
                    partial.isPublic = false
                    partial.token = None
                    try {
                        persist(partial)
                    } catch {
                        case e: ValidationException =>
                            val analysis = analysisRepository.getByName(pluginInstanceId)
                            analysis.map {
                                a =>
                                    analysisRepository.removeById(a.id)
                                    persist(partial)
                            }
                    }

                    val translateMap = clonePluginInstances(instances, bindings, partial)

                    //to make a sensible limit, we need to append sparql construct query with a limit param
                    //CONSTRUCT { ?x ?y ?z } WHERE { ?x ?y ?z  } LIMIT 2

                    lazy val sparqlQueryPluginId = pluginRepository.getByName("SPARQL Query").map(_.id).getOrElse("")
                    val queryInstance = createPluginInstance(sparqlQueryPluginId, partial.id)

                    val persistedAnalysis = analysisRepository.getById(partial.id)
                    persistedAnalysis.map {
                        a =>

                            val persistedInstances = a.pluginInstances

                            persistedInstances.find(_.id == queryInstance.id).map {
                                e =>
                                    setParameterValue(e, ConcreteSparqlQuery.queryParameter, "CONSTRUCT { ?x ?y ?z } WHERE { ?x ?y ?z  } LIMIT 20")
                                    addBinding(partial.id, translateMap.get(o.sourcePluginInstance.id).get, e.id, 0)
                            }
                    }


                    Some(partial.id)
            }.getOrElse(None)
        }
    }
}
