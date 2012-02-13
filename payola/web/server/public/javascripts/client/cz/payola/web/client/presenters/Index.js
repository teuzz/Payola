goog.provide('cz.payola.web.client.presenters.Index');
goog.require('cz.payola.web.client.model.graph.Edge');
goog.require('cz.payola.web.client.model.graph.Graph');
goog.require('cz.payola.web.client.model.graph.Vertex');
goog.require('cz.payola.web.client.views.graph.GraphView');
goog.require('scala.collection.immutable.List');
cz.payola.web.client.presenters.Index = function() {
var self = this;
self.graphModel = self.initGraph();
self.graphView = new cz.payola.web.client.views.graph.GraphView(self.graphModel, document.getElementById('canvas-holder'));
};
cz.payola.web.client.presenters.Index.prototype.init = function() {
var self = this;
self.graphView.initControls();
self.graphView.redrawAll();

};
cz.payola.web.client.presenters.Index.prototype.initGraph = function() {
var self = this;
var v0 = new cz.payola.web.client.model.graph.Vertex('0');
var v1 = new cz.payola.web.client.model.graph.Vertex('1');
var v2 = new cz.payola.web.client.model.graph.Vertex('2');
var v3 = new cz.payola.web.client.model.graph.Vertex('3');
var v4 = new cz.payola.web.client.model.graph.Vertex('4');
var v5 = new cz.payola.web.client.model.graph.Vertex('5');
var v6 = new cz.payola.web.client.model.graph.Vertex('6');
var v7 = new cz.payola.web.client.model.graph.Vertex('7');
var v8 = new cz.payola.web.client.model.graph.Vertex('8');
var v9 = new cz.payola.web.client.model.graph.Vertex('9');
var v10 = new cz.payola.web.client.model.graph.Vertex('10');
var v11 = new cz.payola.web.client.model.graph.Vertex('11');
var v12 = new cz.payola.web.client.model.graph.Vertex('12');
var v13 = new cz.payola.web.client.model.graph.Vertex('13');
var v14 = new cz.payola.web.client.model.graph.Vertex('14');
var v15 = new cz.payola.web.client.model.graph.Vertex('15');
var v16 = new cz.payola.web.client.model.graph.Vertex('16');
var v17 = new cz.payola.web.client.model.graph.Vertex('17');
var v18 = new cz.payola.web.client.model.graph.Vertex('18');
var v19 = new cz.payola.web.client.model.graph.Vertex('19');
var vertices = scala.collection.immutable.List.$apply(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19);
var edges = scala.collection.immutable.List.$apply(new cz.payola.web.client.model.graph.Edge('0', v0, v1), new cz.payola.web.client.model.graph.Edge('1', v0, v2), new cz.payola.web.client.model.graph.Edge('2', v0, v9), new cz.payola.web.client.model.graph.Edge('3', v0, v11), new cz.payola.web.client.model.graph.Edge('4', v0, v16), new cz.payola.web.client.model.graph.Edge('5', v1, v5), new cz.payola.web.client.model.graph.Edge('6', v1, v6), new cz.payola.web.client.model.graph.Edge('7', v2, v3), new cz.payola.web.client.model.graph.Edge('8', v2, v5), new cz.payola.web.client.model.graph.Edge('9', v2, v6), new cz.payola.web.client.model.graph.Edge('10', v2, v8), new cz.payola.web.client.model.graph.Edge('11', v3, v4), new cz.payola.web.client.model.graph.Edge('12', v3, v5), new cz.payola.web.client.model.graph.Edge('13', v3, v11), new cz.payola.web.client.model.graph.Edge('14', v4, v7), new cz.payola.web.client.model.graph.Edge('15', v4, v8), new cz.payola.web.client.model.graph.Edge('16', v4, v11), new cz.payola.web.client.model.graph.Edge('17', v5, v6), new cz.payola.web.client.model.graph.Edge('18', v5, v12), new cz.payola.web.client.model.graph.Edge('19', v6, v7), new cz.payola.web.client.model.graph.Edge('20', v6, v9), new cz.payola.web.client.model.graph.Edge('21', v7, v9), new cz.payola.web.client.model.graph.Edge('22', v8, v9), new cz.payola.web.client.model.graph.Edge('23', v8, v16), new cz.payola.web.client.model.graph.Edge('24', v9, v10), new cz.payola.web.client.model.graph.Edge('25', v9, v13), new cz.payola.web.client.model.graph.Edge('26', v9, v15), new cz.payola.web.client.model.graph.Edge('27', v10, v11), new cz.payola.web.client.model.graph.Edge('28', v10, v12), new cz.payola.web.client.model.graph.Edge('29', v11, v13), new cz.payola.web.client.model.graph.Edge('30', v11, v18), new cz.payola.web.client.model.graph.Edge('31', v11, v19), new cz.payola.web.client.model.graph.Edge('32', v12, v13), new cz.payola.web.client.model.graph.Edge('33', v13, v14), new cz.payola.web.client.model.graph.Edge('34', v13, v19), new cz.payola.web.client.model.graph.Edge('35', v15, v16), new cz.payola.web.client.model.graph.Edge('36', v15, v17), new cz.payola.web.client.model.graph.Edge('37', v16, v17));
return new cz.payola.web.client.model.graph.Graph(vertices, edges);

};
cz.payola.web.client.presenters.Index.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.presenters.Index', []);