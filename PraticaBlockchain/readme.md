### Prática de Blockchain

- Neste trabalho foi utilizado blockchain para simular o registro de votos de uma eleição para presidente de um país. 

- Para este trabalho, foi utilizado uma estrutura de blocos em cadeia, de modo que cada voto da eleição é representado por um bloco.  A estrutura do bloco é:

 > id: identificador do voto
 > data : representa os dado do voto, constituído por data, hora e nomo do presidente votado.
 > hash: código do hash do voto atual :
 > previousHash: código do hash do voto anterior :

- Os blocos gerados são salvos em um arquivo JSON. Depois, os blocos são lidos do arquivo JSON e validados.

- Na conhecimento adiquirido sobre blockchain, foi possível observar que para válidar uma cadeia deve-se verificar, se o ultimo bloco esta válido. Ou seja, se ao gerar seu hash, foi utilizado o hash do anterior e assim sucessivamente. Essa verificação é feita em todos os blocos da cadeia, até que todos seja considerados válidos.