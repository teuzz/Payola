package s2js.compiler.components

import s2js.compiler.ScalaToJsException
import scala.tools.nsc.Global
import scala.collection.mutable
import scala.collection.mutable.LinkedHashMap
import scala.tools.nsc.io.AbstractFile

/** A compiler of PackageDef objects */
class PackageDefCompiler(val global: Global, private val sourceFile: AbstractFile, val packageDef: Global#PackageDef)
{
    /** The dependency manager. */
    val dependencyManager = new DependencyManager(this)

    /** An unique id generator. */
    private var uniqueId = 0;

    /**
      * Returns whether the specified symbol is an internal symbol that mustn't be used in the JavaScript.
      * @param symbol The symbol to check.
      * @return True if the symbol is internal, false otherwise.
      */
    def symbolIsInternal(symbol: Global#Symbol): Boolean = {
        val internalPackageNames = Set(
            "s2js.adapters.js",
            "scala.reflect"
        )
        val internalTypeNames = Set(
            "java.lang.Object",
            "scala.Any",
            "scala.AnyRef",
            "scala.Equals",
            "scala.package.Throwable",
            "scala.Predef",
            "scala.ScalaObject",
            "scala.Serializable"
        )
        val internalTypePatterns = Set(
            """^scala.runtime.AbstractFunction[0-9]+$""".r
        )

        symbol.tpe.baseClasses.exists(_.fullName == "scala.AnyVal") ||
            internalPackageNames.exists(symbol.fullName.startsWith(_)) ||
            internalTypeNames.contains(symbol.fullName) ||
            internalTypePatterns.exists(_.findAllIn(symbol.fullName).nonEmpty)
    }

    /**
      * Returns whether the specified symbol is compiled within the current compilation unit.
      * @param symbol The symbol to check.
      * @return True if the symbol is compiled, false otherwise.
      */
    def symbolIsCompiled(symbol: Global#Symbol): Boolean = {
        symbol.sourceFile != null && symbol.sourceFile.name == sourceFile.name
    }

    /**
      * Finds possible package replacement in the symbol full name.
      * @param symbol The symbol in whose name to search for the replacement.
      * @return The replacement Some(oldPackage, newPackage) if such was found, None oterwise.
      */
    def symbolPackageReplacement(symbol: Global#Symbol): Option[(String, String)] = {
        // Ordered by transformation priority (if A is a prefix of B, then the A should be first).
        val packageReplacementMap = LinkedHashMap(
            "java.lang" -> "scala",
            "scala.this" -> "scala",
            "s2js.adapters.js.browser" -> "",
            "s2js.adapters.js.dom" -> "",
            "s2js.adapters" -> "",
            "s2js.runtime" -> ""
        )

        packageReplacementMap.find(r => symbol.fullName.startsWith(r._1))
    }

    /**
      * Returns JavaScript name of a symbol. If the symbol is local, then local JavaScript name is returned. Otherwise
      * fully qualified JavaScript name is returned.
      * @param symbol The symbol whose name should be returned.
      * @return The name.
      */
    def getSymbolJsName(symbol: Global#Symbol): String = {
        if (symbol.isLocal) getSymbolLocalJsName(symbol) else getSymbolFullJsName(symbol)
    }

    /**
      * Returns fully qualified JavaScript name of a symbol.
      * @param symbol The symbol whose name should be returned.
      * @return The name.
      */
    def getSymbolFullJsName(symbol: Global#Symbol): String = {
        var name = symbol.fullName;

        // Perform the namespace transformation (use the longest matching namespace).
        val replacement = symbolPackageReplacement(symbol)
        if (replacement.isDefined) {
            val (oldPackage, newPackage) = replacement.get

            name = name.stripPrefix(oldPackage)
            if (newPackage.isEmpty && name.startsWith(".")) {
                name = name.drop(1)
            }
            name = newPackage + name
        }

        // Drop the "package" package that isn't used in the JavaScript.
        name.replace(".package", "")
    }

    /**
      * Returns JavaScript name of a symbol that should be used in a local scope.
      * @param symbol The symbol whose name should be returned.
      * @return The name.
      */
    def getSymbolLocalJsName(symbol: Global#Symbol): String = {
        getLocalJsName(symbol.name.toString.trim, !symbol.isMethod && symbol.isSynthetic)
    }

    /**
      * Returns JavaScript name corresponding to the specified scala name.
      * @param name The scala name that should be converted.
      * @param forcePrefix Whether the name prefix is enforced. Default false.
      * @return The name.
      */
    def getLocalJsName(name: String, forcePrefix: Boolean = false): String = {
        val jsKeywords = List(
            "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "debugger",
            "default", "delete", "do", "double", "else", "enum", "export", "extends", "false", "final", "finally",
            "float", "for", "function", "goto", "if", "implements", "import", "in", "instanceof", "int", "interface",
            "long", "native", "new", "null", "package", "private", "private", "public", "return", "short", "static",
            "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "typeof", "var",
            "void", "volatile", "while", "with"
        )
        val jsDefaultMembers = List(
            "constructor", "hasOwnProperty", "isPrototypeOf", "propertyIsEnumerable", "apply", "arguments", "call",
            "prototype", "superClass_", "metaClass_"
        )

        // Synthetic symbols get a prefix to avoid name collision with other symbols. Also if the symbol name is a js
        // keyword then it gets the prefix.
        if (forcePrefix || jsKeywords.contains(name) || jsDefaultMembers.contains(name)) {
            "$" + name
        } else {
            name
        }
    }

    /**
      * Compiles the PackageDef.
      * @return The compiled JavaScript source.
      */
    def compile(): String = {
        val buffer = new mutable.ListBuffer[String]

        // Retrieve the PackageDef structure.
        val structure = dependencyManager.getPackageDefStructure
        val graph = structure.classDefDependencyGraph

        // Compile the ClassDef object in the topological order.
        while (graph.nonEmpty && graph.exists(_._2.isEmpty)) {
            // Sort the compilable ClassDef objects by their keys, so the result is "deterministic".
            val classDefKey = graph.toArray.filter(_._2.isEmpty).map(_._1).sortBy(x => x).head

            // Compile the ClassDef.
            val classDef = structure.classDefMap.get(classDefKey).get
            if (symbolIsCompiled(classDef.symbol)) {
                ClassDefCompiler(this, classDef).compile(buffer)
            }

            // Remove the compiled ClassDef from dependencies of all ClassDef objects, that aren't compiled yet. Also 
            // remove it from the working sets.
            graph.foreach(_._2 -= classDefKey)
            graph -= classDefKey
            structure.classDefMap -= classDefKey
        }

        // If there are some ClassDef objects left, then there is a cyclic dependency.
        if (graph.nonEmpty) {
            throw new ScalaToJsException("Cyclic dependency in the class/object dependency graph involving %s.".format(
                graph.head._1
            ))
        }

        // Compile the dependencies
        dependencyManager.compileDependencies(buffer)

        buffer.mkString
    }

    /**
      * Returns an unique id (unique within the packageDef compilation)
      * @return The unique id.
      */
    def getUniqueId(): Int = {
        uniqueId += 1
        uniqueId
    }
}