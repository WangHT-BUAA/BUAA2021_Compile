.data
array: .space 8
str1: .asciiz ""
str0: .asciiz "Hello World\n"
.text
Main:
la $a0 , str0
li $v0 , 4
syscall
li $v0 , 5
syscall
li $t0 , 0
sw $v0 , array($t0)
la $a0 , str1
li $v0 , 4
syscall
li $t0 , 0
lw $a0 , array($t0)
li $v0 , 1
syscall
li $s0 , 0
li $t0 , 4
sw $s0 , array($t0)
li $v0 , 10
syscall
li $t0 , 4
lw $v0 , array($t0)
jr $ra
