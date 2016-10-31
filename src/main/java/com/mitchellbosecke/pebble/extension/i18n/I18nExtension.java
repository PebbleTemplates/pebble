/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.i18n;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NoOpExtension;

import java.util.HashMap;
import java.util.Map;

public class I18nExtension extends AbstractExtension {

    private final Function i18nFunction;

    private I18nExtension() {
        this.i18nFunction = new i18nFunction();
    }

    private I18nExtension(Function i18nFunction) {
        this.i18nFunction = i18nFunction;
    }

    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();
        functions.put("i18n", i18nFunction);
        return functions;
    }

    /**
     * This {@link Builder} is used to enable/disable the default i18n support
     */
    public static class Builder{

        private final PebbleEngine.Builder parentBuilder;

        private boolean enabled = true;
        private Function i18nFunction = null;

        /**
         * @param builder an instance of {@link PebbleEngine.Builder} that will be returned
         *                when calling Builder{@link #and()}
         */
        public Builder(PebbleEngine.Builder builder){
            this.parentBuilder = builder;
        }

        /**
         * this method enables any {@link I18nExtension} functionality
         *
         * @return the {@link Builder} itself
         */
        public Builder enable() {
            enabled = true;
            return this;
        }

        /**
         * this method disables any {@link I18nExtension} functionality
         *
         * @return the {@link Builder} itself
         */
        public Builder disable() {
            enabled = false;
            return this;
        }

        /**
         * @param function a custom function to use as i18n function
         * @return the {@link Builder} itself
         */
        public Builder useFunction(Function function) {
            this.i18nFunction = function;
            return this;
        }

        /**
         * this method returns the original {@link PebbleEngine.Builder}
         * for further configuration
         *
         * @return the {@link PebbleEngine.Builder} that called the {@link PebbleEngine.Builder#i18n()} method
         */
        public PebbleEngine.Builder and(){
            return parentBuilder;
        }

        /**
         * this methods builds the {@link I18nExtension} according to the
         * configuration. If the extension was disabled it'll return an
         * instance of {@link NoOpExtension}.
         *
         * @return either an {@link I18nExtension} or a {@link NoOpExtension}
         */
        public Extension build() {
            if(enabled){
                if(this.i18nFunction != null) {
                    return new I18nExtension(this.i18nFunction);
                } else {
                    return new I18nExtension();
                }
            } else {
                return new NoOpExtension();
            }
        }

    }

}
