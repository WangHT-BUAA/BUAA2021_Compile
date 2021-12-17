.data
array: .space 0
str1: .asciiz ""
str7: .asciiz ".\n"
str2: .asciiz "\n"
str6: .asciiz " is not a prime, with factor "
str8: .asciiz " is a prime.\n"
str4: .asciiz "yoga"
str9: .asciiz "The "
str0: .asciiz "18375200\n"
str3: .asciiz "ppgod7mi"
str10: .asciiz "th Fibonacci num is "
str11: .asciiz "."
str5: .asciiz "2333\n\n\n\n"
.text
Main:
la $a0 , str0
li $v0 , 4
syscall
li $v0 , 5
syscall
sw $v0 , -0($sp)
li $s0 , 3
sw $s0 , -40($sp)
lw $t1 , -40($sp)
add $s0 , $0 , $t1
sw $s0 , -44($sp)
lw $t1 , -44($sp)
sub $s0 , $0 , $t1
sw $s0 , -48($sp)
lw $t1 , -48($sp)
add $s0 , $0 , $t1
sw $s0 , -52($sp)
lw $t1 , -0($sp)
lw $t2 , -52($sp)
mul $s0 , $t1 , $t2
sw $s0 , -56($sp)
lw $s0 , -56($sp)
sw $s0 , -4($sp)
la $a0 , str1
li $v0 , 4
syscall
lw $a0 , -4($sp)
li $v0 , 1
syscall
la $a0 , str2
li $v0 , 4
syscall
li $s0 , 10086
sw $s0 , -60($sp)
lw $t1 , -4($sp)
lw $t2 , -60($sp)
div $t1 , $t2
mfhi $s0
sw $s0 , -64($sp)
li $s0 , 2333
sw $s0 , -68($sp)
lw $t1 , -64($sp)
lw $t2 , -68($sp)
div $t1 , $t2
mfhi $s0
sw $s0 , -72($sp)
lw $s0 , -72($sp)
sw $s0 , -8($sp)
la $a0 , str1
li $v0 , 4
syscall
lw $a0 , -8($sp)
li $v0 , 1
syscall
la $a0 , str2
li $v0 , 4
syscall
lw $t1 , -4($sp)
lw $t2 , -4($sp)
mul $s0 , $t1 , $t2
sw $s0 , -76($sp)
lw $t1 , -8($sp)
lw $t2 , -76($sp)
mul $s0 , $t1 , $t2
sw $s0 , -80($sp)
li $s0 , 326
sw $s0 , -84($sp)
lw $t1 , -80($sp)
lw $t2 , -84($sp)
div $s0 , $t1 , $t2
sw $s0 , -88($sp)
lw $s0 , -88($sp)
sw $s0 , -12($sp)
la $a0 , str1
li $v0 , 4
syscall
lw $a0 , -12($sp)
li $v0 , 1
syscall
la $a0 , str2
li $v0 , 4
syscall
la $a0 , str3
li $v0 , 4
syscall
la $a0 , str2
li $v0 , 4
syscall
li $s0 , 4396
sw $s0 , -92($sp)
lw $a0 , -92($sp)
li $v0 , 1
syscall
la $a0 , str4
li $v0 , 4
syscall
li $s0 , 7
sw $s0 , -96($sp)
lw $a0 , -96($sp)
li $v0 , 1
syscall
la $a0 , str2
li $v0 , 4
syscall
la $a0 , str5
li $v0 , 4
syscall
li $v0 , 5
syscall
sw $v0 , -16($sp)
li $s0 , 2
sw $s0 , -100($sp)
lw $s0 , -100($sp)
sw $s0 , -20($sp)
if0:
li $s0 , 2
sw $s0 , -104($sp)
lw $t1 , -16($sp)
lw $t2 , -104($sp)
sge $s0 , $t1 , $t2
sw $s0 , -108($sp)
lw $t1 , -108($sp)
beq $t1 , $0 , if0_end
li $s0 , 1
sw $s0 , -112($sp)
lw $s0 , -112($sp)
sw $s0 , -24($sp)
j if0_else_end
if0_end:
li $s0 , 0
sw $s0 , -116($sp)
lw $s0 , -116($sp)
sw $s0 , -24($sp)
if0_else_end:
while0:
li $s0 , 2
sw $s0 , -120($sp)
lw $t1 , -16($sp)
lw $t2 , -120($sp)
div $s0 , $t1 , $t2
sw $s0 , -124($sp)
lw $t1 , -20($sp)
lw $t2 , -124($sp)
sle $s0 , $t1 , $t2
sw $s0 , -128($sp)
lw $t1 , -128($sp)
beq $t1 , $0 , while0_end
if1:
lw $t1 , -16($sp)
lw $t2 , -20($sp)
div $t1 , $t2
mfhi $s0
sw $s0 , -132($sp)
li $s0 , 0
sw $s0 , -136($sp)
lw $t1 , -132($sp)
lw $t2 , -136($sp)
seq $s0 , $t1 , $t2
sw $s0 , -140($sp)
lw $t1 , -140($sp)
beq $t1 , $0 , if1_end
li $s0 , 0
sw $s0 , -144($sp)
lw $s0 , -144($sp)
sw $s0 , -24($sp)
j while0_end
if1_end:
li $s0 , 1
sw $s0 , -148($sp)
lw $t1 , -20($sp)
lw $t2 , -148($sp)
add $s0 , $t1 , $t2
sw $s0 , -152($sp)
lw $s0 , -152($sp)
sw $s0 , -20($sp)
j while0
while0_end:
if2:
li $s0 , 0
sw $s0 , -156($sp)
lw $t1 , -24($sp)
lw $t2 , -156($sp)
seq $s0 , $t1 , $t2
sw $s0 , -160($sp)
lw $t1 , -160($sp)
beq $t1 , $0 , if2_end
la $a0 , str1
li $v0 , 4
syscall
lw $a0 , -16($sp)
li $v0 , 1
syscall
la $a0 , str6
li $v0 , 4
syscall
lw $a0 , -20($sp)
li $v0 , 1
syscall
la $a0 , str7
li $v0 , 4
syscall
j if2_else_end
if2_end:
la $a0 , str1
li $v0 , 4
syscall
lw $a0 , -16($sp)
li $v0 , 1
syscall
la $a0 , str8
li $v0 , 4
syscall
if2_else_end:
li $v0 , 5
syscall
sw $v0 , -28($sp)
li $s0 , 1
sw $s0 , -164($sp)
lw $s0 , -164($sp)
sw $s0 , -32($sp)
li $s0 , 1
sw $s0 , -168($sp)
lw $s0 , -168($sp)
sw $s0 , -36($sp)
li $s0 , 2
sw $s0 , -172($sp)
lw $s0 , -172($sp)
sw $s0 , -8($sp)
while1:
lw $t1 , -8($sp)
lw $t2 , -28($sp)
slt $s0 , $t1 , $t2
sw $s0 , -176($sp)
lw $t1 , -176($sp)
beq $t1 , $0 , while1_end
lw $t1 , -32($sp)
lw $t2 , -36($sp)
add $s0 , $t1 , $t2
sw $s0 , -180($sp)
li $s0 , 1000007
sw $s0 , -184($sp)
lw $t1 , -180($sp)
lw $t2 , -184($sp)
div $t1 , $t2
mfhi $s0
sw $s0 , -188($sp)
lw $s0 , -188($sp)
sw $s0 , -12($sp)
lw $s0 , -36($sp)
sw $s0 , -32($sp)
lw $s0 , -12($sp)
sw $s0 , -36($sp)
li $s0 , 1
sw $s0 , -192($sp)
lw $t1 , -8($sp)
lw $t2 , -192($sp)
add $s0 , $t1 , $t2
sw $s0 , -196($sp)
lw $s0 , -196($sp)
sw $s0 , -8($sp)
j while1
while1_end:
la $a0 , str9
li $v0 , 4
syscall
lw $a0 , -28($sp)
li $v0 , 1
syscall
la $a0 , str10
li $v0 , 4
syscall
lw $a0 , -36($sp)
li $v0 , 1
syscall
la $a0 , str11
li $v0 , 4
syscall
li $s0 , 0
sw $s0 , -200($sp)
li $v0 , 10
syscall
lw $v0 , -200($sp)
jr $ra
