package org.hibernate.dialect.custom;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.id.custom.TableNameSequenceGenerator;

/**
 * A custom oracle dialect that used the tablenamesequencegenerator,
 * which automatically resolves correct sequence names
 * for hibernate entity/oracle table
 * 
 * ref: http://community.jboss.org/wiki/Customsequences
 * ref: https://forum.hibernate.org/viewtopic.php?t=977266&highlight=default+sequence+name
 * @author cm325
 *
 */
public class CustomOracle10gDialect extends Oracle10gDialect {
	  public Class getNativeIdentifierGeneratorClass() {
	    return TableNameSequenceGenerator.class;
	  }
	}
