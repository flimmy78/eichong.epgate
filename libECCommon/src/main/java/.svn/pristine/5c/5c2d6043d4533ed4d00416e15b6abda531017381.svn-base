package com.ec.config;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 所有游戏中的对象都应该继承该类
 * @author haojian
 * Apr 18, 2012 7:06:32 PM
 * 
 * @Id
	@Column
	@GeneratedValue(strategy=GenerationType.AUTO)
 * 
 * 
 * @Column
 * 
 * @Transient
 * 
 */
public abstract class GameObject implements Serializable, Cloneable {
	
	private static final Logger logger = LoggerFactory
			.getLogger(GameObject.class);
	
	public static final long serialVersionUID = 1L;

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error("clone exception,e.getMessage:{}",e.getMessage());
		}
		return obj;
	}

}
