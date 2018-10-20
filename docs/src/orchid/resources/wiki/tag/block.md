---
---

# `block`
The `block` tag performs two functions. If used in a parent template, it will designate a section as being
allowed to be overriden by a child template. If used in a child template, it will override the content
originally declared in the parent template. See the	{{ anchor('extends') }} tag for a more detailed explanation on how
to implement template inheritance.

The contents of a block will only be used if a child template does not override it. It is often useful to
define empty blocks as placeholders for content to be provided by a child template.

The	`block` tag is immediately followed by the name of the block. This name will be the same name
the child template uses to override it. The `endblock` tag can optionally contain the block's name for readability.

In the following example we create a block with the name 'header':
```twig
{% verbatim %}
{% block header %}
	<h1> Introduction </h1>
{% endblock header %}
{%- endverbatim %}
```
A child template should not have any content outside of blocks. A child template is only used to override
blocks of a parent template.