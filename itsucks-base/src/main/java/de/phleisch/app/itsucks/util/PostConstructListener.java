package de.phleisch.app.itsucks.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import de.phleisch.app.itsucks.job.impl.CleanJobManagerImpl;

/**
 * Listener for Guice to support @PostConstruct annotations.
 * @author olli
 *
 */
public class PostConstructListener implements TypeListener{

	private static Log logger = LogFactory.getLog(CleanJobManagerImpl.class);

    @Override
    public <I> void hear(TypeLiteral<I> iTypeLiteral,final TypeEncounter<I> iTypeEncounter) {

        Class<? super I> type = iTypeLiteral.getRawType();

        List<Method> allMethods = new ArrayList<Method>();
        allMethods.addAll(Arrays.asList(type.getDeclaredMethods()));
        Class<?> superclass = type.getSuperclass();
        while(superclass != null) {
        	allMethods.addAll(Arrays.asList(superclass.getDeclaredMethods()));
        	superclass = superclass.getSuperclass();
        }
        
        for (Method method : allMethods) {
			if(method.isAnnotationPresent(PostConstruct.class)) {
				
                if (!(method.getReturnType().equals(Void.TYPE) && method.getParameterTypes().length == 0)) {
                    logger.warn("Only VOID methods having 0 parameters are supported by the PostConstruct annotation!" +
                            "method " + method.getName() + " skipped!");
                    return;
                }
                iTypeEncounter.register(new PostConstructInvoker<I>(method));
			}
		}
    }

    class PostConstructInvoker<I> implements InjectionListener<I>{

        private Method method;

        public PostConstructInvoker(Method method) {
            this.method = method;
        }

        @Override
        public void afterInjection(I o) {
            try {
                method.invoke(o);
            } catch (Throwable e) {
                logger.error(e);
            }
        }
    }
}
