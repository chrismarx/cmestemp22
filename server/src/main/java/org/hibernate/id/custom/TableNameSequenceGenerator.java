package org.hibernate.id.custom;

import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

/**
 * ref - http://community.jboss.org/wiki/Customsequences
 * @author cm325
 *
 * Since es is setup with a mappersuperclass, we can't override the sequencegenerator in subclasses
 * so this approach works, as long as no tables need custom sequence names 
 */
public class TableNameSequenceGenerator extends SequenceGenerator {
	 
    /**
     * If the parameters do not contain a {@link SequenceGenerator#SEQUENCE} name, we
     * assign one based on the table name.
     */
    public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
        //if(params.getProperty(SEQUENCE) == null || params.getProperty(SEQUENCE).length() == 0) {
            String tableName = params.getProperty(PersistentIdentifierGenerator.TABLE);
            if(tableName != null) {
                String seqName = tableName + "_id_seq";
                params.setProperty(SEQUENCE, seqName);               
            }
        //}
        super.configure(type, params, dialect);
    }
}