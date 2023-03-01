# TheAnimeDatabase

## DESCRIPCIÓN

Es una aplicacion que usa la API de [MyAnimeList](https://myanimelist.net/apiconfig/references/api/v2) para buscar y mostrar los animes ordenados por ranking. Usa autentificacion OAuth y necesitas una [cuenta de MAL](https://myanimelist.net/register.php?from=%2F) para acceder a la app. La app guarda el token de acceso en las preferencias del dispositivo y no vuelve a pedir iniciar sesion de nuevo.

### Pantallas

Hay varias pantallas:
* Login de usuario donde se pide autentificacion OAuth
* Recyclerview con una lista de animes que se van actualizando segun haces scroll (con un delay de 1seg para no sobrecargar la API).
* Informacion de cada anime con detalles avanzados.
* Imagen en maxima resolucion cuando le das a la imagen de preview del anime en la pantalla de detalles.


## ENTREGA

Version en Java. Si no hay nueva version es que no lo he podido convertir a kotlin exitosamente.

**Enlace a la release de github:** [Release](https://github.com/TheCoderGamer/TheAnimeDatabase/releases/tag/Release-1).

**Enlace a el commit de github:** [Commit](https://github.com/TheCoderGamer/TheAnimeDatabase/commit/305ebfaaf9e0703c3f5ef2aa7558f820f16a3d14).



***Ultima edicion: 01/03/2023***
