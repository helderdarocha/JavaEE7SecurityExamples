package br.com.argonavis.javaeesecurity.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Grupo.class)
public abstract class Grupo_ {

	public static volatile SingularAttribute<Grupo, String> nome;
	public static volatile SingularAttribute<Grupo, Integer> id;
	public static volatile ListAttribute<Grupo, Usuario> users;
	public static volatile SingularAttribute<Grupo, String> descricao;

}

