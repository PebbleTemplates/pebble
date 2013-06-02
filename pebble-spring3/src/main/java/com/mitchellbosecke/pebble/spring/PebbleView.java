package com.mitchellbosecke.pebble.spring;

import java.util.Locale;
import java.util.Map;

import javax.naming.Context;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.NestedIOException;
import org.springframework.web.servlet.view.AbstractTemplateView;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class PebbleView extends AbstractTemplateView {

	private PebbleEngine pebbleEngine;

	private PebbleTemplate template;

	/**
	 * Set the PebbleEngine to be used by this view.
	 * <p>
	 * If this is not set, the default lookup will occur: A single PebbleConfig
	 * is expected in the current web application context, with any bean name.
	 * 
	 * @see PebbleConfig
	 */
	public void setPebbleEngine(PebbleEngine pebbleEngine) {
		this.pebbleEngine = pebbleEngine;
	}

	/**
	 * Return the PebbleEngine used by this view.
	 */
	protected PebbleEngine getPebbleEngine() {
		return this.pebbleEngine;
	}

	/**
	 * Invoked on startup. Looks for a single PebbleConfig bean to find the
	 * relevant PebbleEngine for this factory.
	 */
	@Override
	protected void initApplicationContext() throws BeansException {
		super.initApplicationContext();

		if (getPebbleEngine() == null) {
			// No explicit PebbleEngine: try to autodetect one.
			setPebbleEngine(autodetectPebbleEngine());
		}
	}

	/**
	 * Autodetect a PebbleEngine via the ApplicationContext. Called if no
	 * explicit PebbleEngine has been specified.
	 * 
	 * @return the PebbleEngine to use for PebbleViews
	 * @throws BeansException
	 *             if no PebbleEngine could be found
	 * @see #getApplicationContext
	 * @see #setPebbleEngine
	 */
	protected PebbleEngine autodetectPebbleEngine() throws BeansException {
		try {
			PebbleConfig pebbleConfig = BeanFactoryUtils.beanOfTypeIncludingAncestors(getApplicationContext(),
					PebbleConfig.class, true, false);
			return pebbleConfig.getPebbleEngine();
		} catch (NoSuchBeanDefinitionException ex) {
			throw new ApplicationContextException(
					"Must define a single PebbleConfig bean in this web application context "
							+ "(may be inherited): PebbleConfigurer is the usual implementation. "
							+ "This bean may be given any name.", ex);
		}
	}

	@Override
	public boolean checkResource(Locale locale) throws Exception {
		try {
			// Check that we can get the template, even if we might subsequently
			// get it again.
			this.template = getTemplate(getUrl());
			return true;
		} catch (LoaderException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("No Pebble view found for URL: " + getUrl());
			}
			return false;
		} catch (Exception ex) {
			throw new NestedIOException("Could not load Pebble template for URL [" + getUrl() + "]", ex);
		}
	}

	/**
	 * Process the model map by merging it with the Pebble template. Output is
	 * directed to the servlet response.
	 * <p>
	 * This method can be overridden if custom behavior is needed.
	 */
	@Override
	protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		doRender(model, response);
	}

	/**
	 * Expose helpers unique to each rendering operation. This is necessary so
	 * that different rendering operations can't overwrite each other's formats
	 * etc.
	 * <p>
	 * Called by <code>renderMergedTemplateModel</code>. The default
	 * implementation is empty. This method can be overridden to add custom
	 * helpers to the model.
	 * 
	 * @param model
	 *            the model that will be passed to the template for merging
	 * @param request
	 *            current HTTP request
	 * @throws Exception
	 *             if there's a fatal error while we're adding model attributes
	 * @see #renderMergedTemplateModel
	 */
	protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
	}

	/**
	 * Expose helpers unique to each rendering operation. This is necessary so
	 * that different rendering operations can't overwrite each other's formats
	 * etc.
	 * <p>
	 * Called by <code>renderMergedTemplateModel</code>. Default implementation
	 * delegates to <code>exposeHelpers(pebbleContext, request)</code>. This
	 * method can be overridden to add special tools to the context, needing the
	 * servlet response to initialize (see Pebble Tools, for example LinkTool
	 * and ViewTool/ChainedContext).
	 * 
	 * @param pebbleContext
	 *            Pebble context that will be passed to the template
	 * @param request
	 *            current HTTP request
	 * @param response
	 *            current HTTP response
	 * @throws Exception
	 *             if there's a fatal error while we're adding model attributes
	 * @see #exposeHelpers(org.apache.pebble.context.Context,
	 *      HttpServletRequest)
	 */
	protected void exposeHelpers(Context pebbleContext, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		exposeHelpers(pebbleContext, request);
	}

	/**
	 * Expose helpers unique to each rendering operation. This is necessary so
	 * that different rendering operations can't overwrite each other's formats
	 * etc.
	 * <p>
	 * Default implementation is empty. This method can be overridden to add
	 * custom helpers to the Pebble context.
	 * 
	 * @param pebbleContext
	 *            Pebble context that will be passed to the template
	 * @param request
	 *            current HTTP request
	 * @throws Exception
	 *             if there's a fatal error while we're adding model attributes
	 * @see #exposeHelpers(Map, HttpServletRequest)
	 */
	protected void exposeHelpers(Context pebbleContext, HttpServletRequest request) throws Exception {
	}



	/**
	 * Initialize the given tool instance. The default implementation is empty.
	 * <p>
	 * Can be overridden to check for special callback interfaces, for example
	 * the ViewContext interface which is part of the view package of Pebble
	 * Tools. In the particular case of ViewContext, you'll usually also need a
	 * special Pebble context, like ChainedContext which is part of Pebble Tools
	 * too.
	 * <p>
	 * Have a look at {@link PebbleToolboxView}, which pre-implements such a
	 * ViewTool check. This is not part of the standard PebbleView class in
	 * order to avoid a required dependency on the view package of Pebble Tools.
	 * 
	 * @param tool
	 *            the tool instance to initialize
	 * @param pebbleContext
	 *            the Pebble context
	 * @throws Exception
	 *             if initializion of the tool failed
	 * @see #createPebbleContext
	 * @see org.apache.pebble.tools.view.context.ViewContext
	 * @see org.apache.pebble.tools.view.context.ChainedContext
	 * @see PebbleToolboxView
	 */
	protected void initTool(Object tool, Context pebbleContext) throws Exception {
	}

	/**
	 * Render the Pebble view to the given response, using the given Pebble
	 * context which contains the complete template model to use.
	 * <p>
	 * The default implementation renders the template specified by the "url"
	 * bean property, retrieved via <code>getTemplate</code>. It delegates to
	 * the <code>mergeTemplate</code> method to merge the template instance with
	 * the given Pebble context.
	 * <p>
	 * Can be overridden to customize the behavior, for example to render
	 * multiple templates into a single view.
	 * 
	 * @param context
	 *            the Pebble context to use for rendering
	 * @param response
	 *            servlet response (use this to get the OutputStream or Writer)
	 * @throws Exception
	 *             if thrown by Pebble
	 * @see #setUrl
	 * @see #getTemplate()
	 * @see #mergeTemplate
	 */
	protected void doRender(Map<String, Object> context, HttpServletResponse response) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Rendering Pebble template [" + getUrl() + "] in PebbleView '" + getBeanName() + "'");
		}
		mergeTemplate(getTemplate(), context, response);
	}

	/**
	 * Retrieve the Pebble template to be rendered by this view.
	 * <p>
	 * By default, the template specified by the "url" bean property will be
	 * retrieved: either returning a cached template instance or loading a fresh
	 * instance (according to the "cacheTemplate" bean property)
	 * 
	 * @return the Pebble template to render
	 * @throws Exception
	 *             if thrown by Pebble
	 * @see #setUrl
	 * @see #setCacheTemplate
	 * @see #getTemplate(String)
	 */
	protected PebbleTemplate getTemplate() throws Exception {
		// We already hold a reference to the template, but we might want to
		// load it
		// if not caching. Pebble itself caches templates, so our ability to
		// cache templates in this class is a minor optimization only.
		if (this.template != null) {
			return this.template;
		} else {
			return getTemplate(getUrl());
		}
	}

	/**
	 * Retrieve the Pebble template specified by the given name, using the
	 * encoding specified by the "encoding" bean property.
	 * <p>
	 * Can be called by subclasses to retrieve a specific template, for example
	 * to render multiple templates into a single view.
	 * 
	 * @param name
	 *            the file name of the desired template
	 * @return the Pebble template
	 * @throws Exception
	 *             if thrown by Pebble
	 * @see org.apache.pebble.app.PebbleEngine#getTemplate
	 */
	protected PebbleTemplate getTemplate(String name) throws Exception {
		return getPebbleEngine().loadTemplate(name);
	}

	/**
	 * Merge the template with the context. Can be overridden to customize the
	 * behavior.
	 * 
	 * @param template
	 *            the template to merge
	 * @param context
	 *            the Pebble context to use for rendering
	 * @param response
	 *            servlet response (use this to get the OutputStream or Writer)
	 * @throws Exception
	 *             if thrown by Pebble
	 * @see org.apache.pebble.Template#merge
	 */
	protected void mergeTemplate(PebbleTemplate template, Map<String, Object> context, HttpServletResponse response)
			throws Exception {

		response.getWriter().write(template.render(context));

	}


}
