from workersdk import Worker

def main():
    worker = Worker()

    worker.register("step1", lambda payload: f"step1-result: processed {payload}")
    worker.register("step2", lambda payload: f"step2-result: finished with {payload}")

    worker.start()

if __name__ == "__main__":
    main()
