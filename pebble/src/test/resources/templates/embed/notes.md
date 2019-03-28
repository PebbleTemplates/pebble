This is a complete example using the `embed` tag with lots of different scenarios for using the embed, and specifically
how it interacts with the main template hierarchy. Ideally, the embed is a completely isolated template which maintains
its own template hierarchy, and blocks are resolved entirely within its isolated hierarchy. Outside of the embed, blocks
are resolved related to the normal hierarchy, and there is no mixing of the two. 

The majority of these tests were compared to Twig for fidelity, using https://twigfiddle.com/.

## Unit Tests

- test2 - Bare minimum, can an `embed` include a template. Without overriding a block, an `embed` should work identical 
    to an `include`
- test0 - Can an `embed` override a block in an included template
- test1 - Can an `embed` override a block in an included template, using Pebble's string block name syntax
- test3 - Variables can be passed to the included template's scope using `with`. Overridden blocks use the scope of the 
    included template, not the parent template, and can use Pebble's string block name syntax
- test4 - Variables can be passed to the included template's scope using `with`. Overridden blocks use the scope of the 
    included template, not the parent template
- test5 - Multiple blocks can be overridden at once. When using the `block()` function, looked-up blocks will also use
    the overridden block rather than the one in the included template
- test6 - When using the `parent()` function, looked-up blocks will use the parent block in the included template

## Integration Tests

- test7 - The template being embedded may itself extend other templates. This may go many layers deep. 
- test8 - The parent templates of the embedded template may override blocks from their parents
- test9 - The parent templates of the embedded template may override blocks from their parents. Different templates may
    override different blocks simultaneously
- test10 - The embed tab can override blocks at the same time that parent templates do, and they are all resolved 
    properly
- test11 - `block()` and `parent()` functions can be used within the `embed` tag's overridden blocks to refer to it's 
    parent template blocks, which are resolved with overrides properly
- test12 - An embed tag can be used in a template that itself extends another template. The same parent template may
    be used in either case
- test13 - The `extends` tag and `embed` tag maintain different template hierarchies. Blocks are resolved using the 
    proper hierarchy for the scope the `block()` and `parent()` functions are used in
- test14 - Blocks do not bleed from the parent template hierarchy into the embed template hierarchy.

## Error-checking Tests

- test15 - Error thrown if content is added to the embed tag (instead of a block inside the embed)
- test16 - Error thrown if content is added before a block in the the embed tag
- test17 - Error thrown if content is added after a block in the the embed tag
- test18 - Error thrown if content is added between blocks in the the embed tag