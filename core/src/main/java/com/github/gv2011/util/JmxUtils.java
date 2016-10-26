package com.github.gv2011.util;


import static com.github.gv2011.util.ex.Exceptions.tolerate;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JmxUtils {

	private static final Logger LOG = LoggerFactory.getLogger(JmxUtils.class);

	private static final Object lock = new Object();
	private static final Map<Class<?>, Integer> lastUsedInstanceNumber = new HashMap<>();
	private static final WeakHashMap<Object,Integer> instanceNumber = new WeakHashMap<>();
	private static final Map<ObjectName,ObjectName> usedNames = new ConcurrentHashMap<>();

	private static final String INSTANCE = "instance_";

	public static ObjectName getJmxName(final Class<?> mBeanClass, final Object mBean){
		ObjectName jmxName = null;
		if(mBean instanceof Named){
			final String name = ((Named)mBean).name();
			if(!name.isEmpty() && !name.startsWith(INSTANCE)){
				try {
					jmxName = getJmxNameInternal(mBeanClass, name);
					final boolean isUsed = usedNames.put(jmxName, jmxName)!=null;
					if(isUsed) jmxName = null;
				} catch(final MalformedObjectNameException e){}
			}
		}
		if(jmxName==null){
			final int number = getNumber(mBeanClass, mBean);
			try {
				jmxName = getJmxNameInternal(mBeanClass, INSTANCE+number);
			} catch (final MalformedObjectNameException e) {throw new IllegalArgumentException(e);}
		}
		return jmxName;
	}


	private static ObjectName getJmxNameInternal(final Class<?> mBeanClass, final String name
	) throws MalformedObjectNameException {
		return new ObjectName(
			mBeanClass.getPackage().getName()+":type="+mBeanClass.getSimpleName()+",name="+name
		);
	}


	private static int getNumber(final Class<?> clazz, final Object mBean) {
		synchronized(lock){
			Integer number = instanceNumber.get(mBean);
			if(number==null){
				number = lastUsedInstanceNumber.get(clazz);
				final int next = number==null?0:number.intValue()+1;
				number = Integer.valueOf(next);
				lastUsedInstanceNumber.put(clazz, number);
				instanceNumber.put(mBean, number);
			}
			return number.intValue();
		}
	}

	public static AutoCloseableNt registerMBean(final Object mBean) {
		return registerMBean(mBean.getClass(), mBean);
	}

	public static AutoCloseableNt registerMBean(final Class<?> mBeanClass, final Object mBean) {
		final ObjectName jmxName = getJmxName(mBeanClass, mBean);
		try {
			ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, jmxName);
			LOG.info("Registered mBean {}.", jmxName);
		} catch (
			InstanceAlreadyExistsException
			| MBeanRegistrationException
			| NotCompliantMBeanException e
		) {
			tolerate(e);
		}
		return () -> {
    	try {
    		ManagementFactory.getPlatformMBeanServer().unregisterMBean(jmxName);
    		usedNames.remove(jmxName);
    	} catch (MBeanRegistrationException | InstanceNotFoundException e){
    	  tolerate(e);
    	}
    };
	}


}
