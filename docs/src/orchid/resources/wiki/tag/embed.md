---
---

# `embed`

The `embed` tag allows you to insert the rendered output of another template directly into the current template, while 
overriding some of its blocks. It effectively combines the behavior of {{anchor('include')}} with that of 
{{anchor('extends')}} for creating reusable, yet flexible, template fragments, or for composing micro-layouts.

{% verbatim %}

For example, imagine building a template `card.peb` as a reusable component in your layout. All cards should have the 
same markup, but the content can change drastically throughout your site. `card.peb` might then look like:

```twig
// card.peb
<div class="card">
    {% block cardContent %}
    {% endblock %}
</div>
```

Now, you can include that template elsewhere in your layout, and override the `cardContent` block to "inject" rich 
content into that template at the call-side. For example, you may want to display a grid of your store's most popular 
products as cards, with the last card linking to the full catalog. Embedding `card.peb` and overriding the `cardContent`
block ensures that the markup for both types of cards are always the same, even though what's displayed on each card is 
quite different.

```twig
// layout.peb

{% for product in popularProducts %}
    {% embed 'card.peb' %}
        {% block cardContent %}
            <h1>{{ product.name }}</h1>
            <p>{{ product.description }}</p>
        {% endblock %}
    {% endembed %}
{% endfor %}

{% embed 'card.peb' %}
    {% block cardContent %}
        <a href="...">See all 100+ products</a>
    {% endblock %}
{% endembed %}
```

Embeds can be used multiple times in the same template, and may also be used in a template that itself extends another.
Each template will then maintain its own block hierarchy. In other words, block overridden within the body of the 
`embed` tag will not accidentally override those defined in the main template, and likewise blocks defined in the main
template or its parent templates will not get mixed with those in the embedded template or its parent templates. 

```twig
// main.peb
{% extends 'base.peb' %}

{% block mainContent %}
    {{ parent() }} {# renders mainContent block from base.peb #}
    {{ block('footer') }} {# renders footer block from base.peb, the global page footer #}
    
    {% embed 'card.peb' %}
        {% block mainContent %}
            {{ parent() }} {# renders mainContent block from card.peb #}
            {{ block('footer') }} {# renders footer block from card.peb, the card footer (not the global page footer) #}
        {% endblock %}
    {% endembed %}
{% endblock %}
```

## Scope

Embedded templates will have access to the same variables that the current template does.

```twig
Top Content
{% embed "advertisement" %}{% endembed %}
Bottom Content
{% embed "footer" %}{% endembed %}
```

You can add additional variables to the context of the embedded template by passing a map after the `with` keyword. The embedded template will have access to the same variables that the current template does plus the additional ones defined in the map passed after the `with` keyword:

```twig
{% embed "advertisement" with {"foo":"bar"} %}
    {% block title %}
        Ad with title
    {% endblock %}
    {% block content %}
        Ad with title
    {% endblock %}
{% endembed %}
```

## Dynamic embed
The `embed` tag will accept an expression to determine the template to embed at runtime. For example:
```twig
{% embed admin ? 'adminFooter' : 'defaultFooter' %}
{% endembed %}
```
{% endverbatim %}
