**Regras de Negócio:** 

* **RN01**: O sistema deve aceitar ROM’s de, no máximo, 3584 bytes;  
* **RN02**: As ROM’s devem ser carregadas na memória a partir do endereço 0x200;  
* **RN03**: A memória total deve ter exatamente 4096 bytes;  
* **RN04:** A tela deve possuir uma resolução fixa de 64x32 pixels;  
* **RN05:** A tela deve ser monocromática, podendo apenas assumir dois estados: ligada (1) ou desligada (0);  
* **RN06:** Um conjunto de sprites representando os caracteres hexadecimais (0 a F) deve ser pré-carregado na área reservada da memória (abaixo de 0x200).  
* **RN07:** O teclado deve ser hexadecimal de 16 teclas, indo de 0x0 até 0xF;  
* **RN08:** O sistema deve possuir dois temporizadores, sendo eles o Delay Timer e o Sound Timer;  
* **RN09:** Ambos os temporizadores, quando maiores que zero, devem decrementar a uma taxa de 60Hz;  
* **RN10:** Enquanto o Sound Timer for maior que zero, um som deve ser produzido;  
* **RN11:** O Program Counter deve ser inicializado a partir do endereço 0x200;  
* **RN12:** O emulador deve possuir 16 registradores de memória gerais de 8 bits, indo de V0 até VF;  
* **RN13**: O registrador de memória VF serve como ‘flag’, sendo responsável por indicar carry, borrow e detecção de colisão, não sendo utilizado diretamente por outros programas;  
* **RN14:** O sistema deve possuir um registrador de índice de 16 bits, anexado de “I“, responsável por operações de leitura e escrita de dados;  
* **RN15:** O stack do sistema serve apenas e exclusivamente para armazenar os endereços de memória a qual o programa deve retornar, após a conclusão de uma subroutine;  
* **RN16:** O comportamento da CPU é definido por 35 opcodes, que são instruções de 2 bytes que devem ser lidas da memória, decodificadas e executadas;  
* **RN17:** A instrução de desenho, “Dxyn”, deve desenhar sprites na tela com base em uma lógica XOR. Se, durante essa operação, qualquer pixel for alterado de 'ligado' para 'desligado', o registrador VF deve ser definido como 1; caso contrário, será definido como 0;  
* **RN18:** Sprites desenhados nas bordas da tela, ultrapassando seu limite, devem ser movidos para o lado oposto;  
* **RN19:** O sistema deve possuir um Stack Pointer de 8 bits, para gerenciar os endereços livres da stack. Ele aponta sempre para o próximo endereço livre.

