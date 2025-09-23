.global _start
.text
_start:
    # Performing logical operations and shifts
    addi x1, x0, 0b1010    # Example value: 10 (binary 1010)
    addi x2, x0, 0b0110    # Example value: 6 (binary 0110)
    
    # Replacing XOR with OR (disjunction)
    or x3, x1, x2          # x3 = x1 OR x2 (instead of XOR)
    
    # Replacing logical shift with arithmetic right shift
    srai x4, x3, 2         # Arithmetic right shift by 2 positions
    
    # Displaying greetings from each team member
    
    # First greeting
    addi a0, x0, 1         # File descriptor: stdout (1)
    la a1, msg1            # Load address of message 1
    addi a2, x0, 42        # Message length
    addi a7, x0, 64        # Syscall: write (64)
    ecall                  # Execute system call
    
    # Second greeting
    addi a0, x0, 1         # File descriptor: stdout (1)
    la a1, msg2            # Load address of message 2
    addi a2, x0, 46        # Message length
    addi a7, x0, 64        # Syscall: write (64)
    ecall                  # Execute system call
    
    # Third greeting
    addi a0, x0, 1         # File descriptor: stdout (1)
    la a1, msg3            # Load address of message 3
    addi a2, x0, 42        # Message length
    addi a7, x0, 64        # Syscall: write (64)
    ecall                  # Execute system call
    
    # Exit with code = sum of last digits of student IDs
    addi a0, x0, 4         # 3 + 1 + 0 = 4
    addi a7, x0, 93        # Syscall: exit (93)
    ecall                  # Execute system call
.data
msg1: .asciz "Hello! I am Berezin Maxim from team 7!!!\n"
msg2: .asciz "Hello! I am Levitskiy Mikhail from team 7!!!\n"
msg3: .asciz "Hello! I am Frolov Nikita from team 7!!!\n"
