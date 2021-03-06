package org.infinispan.query.remote.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.hibernate.search.spi.CustomTypeMetadata;
import org.infinispan.AdvancedCache;
import org.infinispan.objectfilter.impl.ProtobufMatcher;
import org.infinispan.objectfilter.impl.syntax.parser.FilterParsingResult;
import org.infinispan.protostream.descriptors.Descriptor;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.dsl.embedded.impl.JPAFilterAndConverter;
import org.infinispan.query.dsl.embedded.impl.RowProcessor;
import org.infinispan.query.impl.SearchManagerImpl;
import org.infinispan.query.remote.impl.filter.JPAProtobufFilterAndConverter;
import org.infinispan.query.remote.impl.indexing.IndexingMetadata;
import org.infinispan.query.remote.impl.indexing.ProtobufValueWrapper;

/**
 * @author anistor@redhat.com
 * @since 8.0
 */
final class RemoteQueryEngine extends BaseRemoteQueryEngine {

   public RemoteQueryEngine(AdvancedCache<?, ?> cache, boolean isIndexed) {
      super(cache, isIndexed, ProtobufMatcher.class, new ProtobufFieldBridgeProvider());
   }

   @Override
   protected RowProcessor makeProjectionProcessor(Class<?>[] projectedTypes) {
      // Protobuf's booleans are indexed as Strings, so we need to convert them.
      // Collect here the positions of all Boolean projections.
      int[] pos = new int[projectedTypes.length];
      int len = 0;
      for (int i = 0; i < projectedTypes.length; i++) {
         if (projectedTypes[i] == Boolean.class) {
            pos[len++] = i;
         }
      }
      if (len == 0) {
         return null;
      }
      final int[] cols = len < pos.length ? Arrays.copyOf(pos, len) : pos;
      return row -> {
         for (int i : cols) {
            if (row[i] != null) {
               // the Boolean column is actually encoded as a String, so we convert it
               row[i] = "true".equals(row[i]);
            }
         }
         return row;
      };
   }

   @Override
   protected Query makeTypeQuery(Query query, String targetEntityName) {
      return new BooleanQuery.Builder()
            .add(new TermQuery(new Term(QueryFacadeImpl.TYPE_FIELD_NAME, targetEntityName)), BooleanClause.Occur.FILTER)
            .add(query, BooleanClause.Occur.MUST)
            .build();
   }

   @Override
   protected CacheQuery<?> makeCacheQuery(FilterParsingResult<Descriptor> filterParsingResult, org.apache.lucene.search.Query luceneQuery) {
      CustomTypeMetadata customTypeMetadata = new CustomTypeMetadata() {
         @Override
         public Class<?> getEntityType() {
            return ProtobufValueWrapper.class;
         }

         @Override
         public Set<String> getSortableFields() {
            IndexingMetadata indexingMetadata = filterParsingResult.getTargetEntityMetadata().getProcessedAnnotation(IndexingMetadata.INDEXED_ANNOTATION);
            return indexingMetadata != null ? indexingMetadata.getSortableFields() : Collections.emptySet();
         }
      };
      HSQuery hSearchQuery = getSearchFactory().createHSQuery(luceneQuery, customTypeMetadata);
      return ((SearchManagerImpl) getSearchManager()).getQuery(hSearchQuery);
   }

   @Override
   protected JPAFilterAndConverter createFilter(String queryString, Map<String, Object> namedParameters) {
      return isIndexed ? new JPAProtobufFilterAndConverter(queryString, namedParameters) :
            super.createFilter(queryString, namedParameters);
   }

   @Override
   protected Class<?> getTargetedClass(FilterParsingResult<?> parsingResult) {
      return ProtobufValueWrapper.class;
   }
}
