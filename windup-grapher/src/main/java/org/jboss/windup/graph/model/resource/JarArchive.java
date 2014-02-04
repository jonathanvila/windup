package org.jboss.windup.graph.model.resource;

import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("JarArchiveResource")
public interface JarArchive extends Archive {

	@GremlinGroovy("it.out('child').out('javaClassFacet')")
	public Iterable<JavaClass> getJavaClasses();
	
	@GremlinGroovy("it.sideEffect{x=it}.out('child').out('javaClassFacet').or(_().out('implements'),_().out('extends')).in('javaClassFacet').in('child').dedup.filter{it!=x}")
	public Iterable<JarArchive> dependsOnArchives();

	@GremlinGroovy("it.sideEffect{x=it}.out('child').out('javaClassFacet').or(_().in('implements'),_().in('extends')).in('javaClassFacet').in('child').dedup.filter{it!=x}")
	public Iterable<JarArchive> providesForArchives();
	
	@GremlinGroovy("it.out('child').out('xmlResourceFacet')")
	public Iterable<XmlResource> getXmlFiles();
	
}
