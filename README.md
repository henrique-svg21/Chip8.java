# Chip8.java
emulador para sistema chip8 desenvolvido em Java

# Regras de Negócio: 

**RN01**: O sistema deve aceitar ROM’s de, no máximo, 3584 bytes;

**RN02**: As ROM’s devem ser carregadas na memória a partir do endereço 0x200;

**RN03**: A memória total deve ter exatamente 4096 bytes;

**RN04**: A tela deve possuir uma resolução fixa de 64x32 pixels;

**RN05**: A tela deve ser monocromática, podendo apenas assumir dois estados: ligada (1) ou desligada (0);

**RN06**: Um conjunto de sprites representando os caracteres hexadecimais (0 a F) deve ser pré-carregado na área reservada da memória (abaixo de 0x200);

**RN07**: O teclado deve ser hexadecimal de 16 teclas, indo de 0x0 até 0xF;

RN08: O sistema deve possuir dois temporizadores, sendo eles o Delay Timer e o Sound Timer;

**RN09**: Ambos os temporizadores, quando maiores que zero, devem decrementar a uma taxa de 60Hz;

**RN10**: Enquanto o Sound Timer for maior que zero, um som deve ser produzido;

**RN11**: O Program Counter deve ser inicializado a partir do endereço 0x200;

**RN12**: O emulador deve possuir 16 registradores de memória gerais de 8 bits, indo de V0 até VF;

**RN13**: O registrador de memória VF serve como ‘flag’, sendo responsável por indicar carry, borrow e detecção de colisão, não sendo utilizado diretamente por outros programas;

**RN14**: O sistema deve possuir um registrador de índice de 16 bits, anexado de “I“, responsável por operações de leitura e escrita de dados;

**RN15**: O stack do sistema serve apenas e exclusivamente para armazenar os endereços de memória a qual o programa deve retornar, após a conclusão de uma subroutine;

**RN16**: O comportamento da CPU é definido por 35 opcodes, que são instruções de 2 bytes que devem ser lidas da memória, decodificadas e executadas;

**RN17**: A instrução de desenho, “Dxyn”, deve desenhar sprites na tela com base em uma lógica XOR. Se, durante essa operação, qualquer pixel for alterado de 'ligado' para 'desligado', o registrador VF deve ser definido como 1; caso contrário, será definido como 0;

**RN18**: Sprites desenhados nas bordas da tela, ultrapassando seu limite, devem ser movidos para o lado oposto;

RN19: O sistema deve possuir um Stack Pointer de 8 bits, para gerenciar os endereços livres da stack. Ele aponta sempre para o próximo endereço livre.


# Requisitos Funcionais:

**RF01**: O sistema deve permitir que o usuário mapeie as 16 teclas do Chip-8 para as teclas do teclado do seu computador;

**RF02**: O sistema deve permitir ao usuário pausar e resumir a emulação a qualquer momento;

**RF03**: O sistema deve permitir ao usuário realizar um reset na emulação da ROM atualmente carregada;

**RF04**: O sistema deve implementar um ciclo de CPU para processar as instruções da ROM carregada;

**RF05**: O sistema deve ser capaz de emular o áudio do dispositivo;

**RF06**: O sistema deve possuir interface gráfica para o usuário interagir;

**RF07**: O sistema deve ser capaz de carregar ROM’s selecionadas pelo o usuário;

**RF08**: O sistema deve incluir um debugger embutido, como, por exemplo, execução passo a passo;

  **RF08.1**: O sistema deve permitir ao usuário visualizar o gerenciamento de memória;
  
  **RF08.2**: O sistema deve permitir ao usuário visualizar os registradores;
  
**RF09**: O sistema deve possuir suporte a várias velocidades de ciclo de CPU, permitindo ao usuário controlar a velocidade de execução.


# Requisitos Não Funcionais:

**RNF01**: O sistema deve rodar de maneira fluida, próximo aos 60 quadros por segundo, mesmo em hardwares de menor desempenho;

**RNF02**: O sistema deve ser intuitivo para o usuário;

**RNF03**: O sistema deve ser robusto e tolerável a erros, lidando adequadamente com falhas como o carregamento de ROMs corrompidas ou a execução de instruções desconhecidas, sem travar inesperadamente;

**RNF04**: O sistema deve possuir um código-fonte modular, separando organizadamente as diferentes responsabilidades;

**RNF05**: O sistema deve possuir um código-fonte que segue os padrões de codificação da linguagem Java;

**RNF06**: O sistema deve possuir uma vasta documentação, explicando o funcionamento do mesmo;

**RNF07**: O sistema deve ser executável em qualquer dispositivo que possua uma Java Virtual Machine (JVM);

**RNF08**: O sistema deve ser compatível com o conjunto padrão de ROM’s do Chip8;


