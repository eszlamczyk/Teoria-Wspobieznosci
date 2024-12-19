import torch
import time

import torch
print(torch.version.cuda)  # Shows the CUDA version PyTorch was built with.

# Define the size of the matrices
matrix_size = 1000

# Create random matrices on CPU
matrix_a_cpu = torch.rand(matrix_size, matrix_size)
matrix_b_cpu = torch.rand(matrix_size, matrix_size)

# Perform and time the operation on the CPU
start_time = time.time()
result_cpu = matrix_a_cpu + matrix_b_cpu
cpu_time = time.time() - start_time
print(f"Time taken on CPU: {cpu_time:.6f} seconds")

# Check if CUDA is available
if torch.cuda.is_available():
    print("CUDA AVAILABLE")
    print(torch.cuda.get_device_name())
    # Move matrices to GPU
    device = torch.device("cuda")
    matrix_a_gpu = matrix_a_cpu.to(device)
    matrix_b_gpu = matrix_b_cpu.to(device)

    # Warm-up GPU to avoid first-run overhead
    _ = matrix_a_gpu + matrix_b_gpu

    # Perform and time the operation on the GPU
    torch.cuda.synchronize()  # Ensure GPU is ready for timing
    start_time = time.time()
    result_gpu = matrix_a_gpu + matrix_b_gpu
    torch.cuda.synchronize()  # Wait for GPU to complete operation
    gpu_time = time.time() - start_time
    print(f"Time taken on GPU: {gpu_time:.6f} seconds")

    # Move result back to CPU (optional)
    result_gpu_cpu = result_gpu.to("cpu")
else:
    print("CUDA is not available. GPU timing was skipped.")
