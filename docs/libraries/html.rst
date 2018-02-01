HTTP
====

The `HTTP` library allows you to perform HTTP requests. It is equipped with some utilities to allow for simple use of
json web APIs.


HttpClient
~~~~~~~~~~

The ``HttpClient`` allows you to perform the actual requests. You can create a new client using ``HTTP.client`` or
automatically create a new client using one of the root level functions.

``HttpClient.get``
------------------

Perform an `HTTP GET` request. This method is a shortcut for calling `HttpClient.request`_ using ``"GET"`` as the
`method` parameter.

Example
#######
.. code-block:: javascript

   var HTTP = Script.library("HTTP");

   var response = HTTP.get({
     url: "https://baconipsum.com/api/?type=meat-and-filler"
   });

   var body = response.body().asJson();

   // Output the result from bacon ipsum
   Script.info(body[0]);


``HttpClient.head``
-------------------

``HttpClient.post``
-------------------

``HttpClient.delete``
---------------------

``HttpClient.put``
------------------

``HttpClient.patch``
--------------------

.. _HttpClient.request:
``HttpClient.request``
----------------------




