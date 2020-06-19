#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@interface BacktraceAttributes: NSObject 

@end

@implementation BacktraceAttributes
    NSDictionary<NSString *, NSString *> GetAttributes(){
        let processor = try? Processor()
        let processInfo = ProcessInfo.processInfo
        let systemVmMemory = try? MemoryInfo.System()
        let systemSwapMemory = try? MemoryInfo.Swap()
        let processVmMemory = try? MemoryInfo.Process()
        
        return [
            // cpu
            "cpu.idle": processor?.cpuTicks.idle,
            "cpu.nice": processor?.cpuTicks.nice,
            "cpu.user": processor?.cpuTicks.user,
            "cpu.system": processor?.cpuTicks.system,
            "cpu.process.count": processor?.processorSetLoadInfo.task_count,
            "cpu.thread.count": processor?.processorSetLoadInfo.thread_count,
            "cpu.uptime": try? System.uptime(),
            "cpu.count": processInfo.processorCount,
            "cpu.count.active": processInfo.activeProcessorCount,
            "cpu.context": processor?.taskEventsInfo.csw,
            // process
            "process.thread.count": try? ProcessInfo.numberOfThreads(),
            "process.age": try? ProcessInfo.age(),
            // system
            "system.memory.active": systemVmMemory?.active,
            "system.memory.inactive": systemVmMemory?.inactive,
            "system.memory.free": systemVmMemory?.free,
            "system.memory.used": systemVmMemory?.used,
            "system.memory.total": systemVmMemory?.total,
            "system.memory.wired": systemVmMemory?.wired,
            "system.memory.swapins": systemVmMemory?.swapins,
            "system.memory.swapouts": systemVmMemory?.swapouts,
            "system.memory.swap.total": systemSwapMemory?.total,
            "system.memory.swap.used": systemSwapMemory?.used,
            "system.memory.swap.free": systemSwapMemory?.free,
            // vm
            "process.vm.rss.size": processVmMemory?.resident,
            "process.vm.rss.peak": processVmMemory?.residentPeak,
            "process.vm.vma.size": processVmMemory?.virtual
        ]
    }
    
@end