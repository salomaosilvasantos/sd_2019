### Prática de Blockchain

- Neste trabalho foi utilizado *blockchain* para simular o registro de votos de uma eleição para presidente de um país. 

- Para este trabalho, foi utilizado uma estrutura de blocos em cadeia, de modo que cada resgistro de voto da eleição é representado por um bloco.  A estrutura do bloco é:

            > id: identificador do voto.
            > data : representa os dados do voto, constituído por data, hora e nomo do presidente votado.
            > hash: código do hash do voto atual.
            > previousHash: código do hash do voto anterior.

- Os blocos gerados são salvos em um arquivo JSON. Depois, os blocos são lidos do arquivo JSON e validados.


- No conhecimento adiquirido sobre *blockchain*, existem dois tipos básicos de registros: *blocos e transações*. Começando com um bloco inicial(chamado de bloco gênese) que registra o estado inicial do banco de dados, seguido pelos blocos subsequentes, cada qual contém, um grupo de transações já validadas. Cada bloco da cadeia, exceto o gênese, contém um hash do bloco anterior, criando um encadeamento entre eles e garantindo a integridade da informação, já que é impossivel alterar blocos antigos sem alterar todos os blocos subsequentes. Portanto, pode-se entender um blockchain como um estado inicial seguido de um certo número de funções de transição, agrupados em blocos. De fato, o estado atual do banco de dados está contido em um blockchain apenas de maneira abstrata, sendo necessário que cada nó determine tal estado partindo do inicial e aplicando as subsequentes transações.