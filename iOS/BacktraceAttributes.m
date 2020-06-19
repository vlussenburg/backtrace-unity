//
//  BacktraceAttributes.m
//  Unity-Plugin
//
//  Created by Konrad Dysput on 19/06/2020.
//  Copyright © 2020 Backtrace. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import <mach/mach.h>
#import <mach/mach_host.h>



@interface BacktraceAttributes: NSObject
+ (BacktraceAttributes*) create;
- (void) readMemoryParameters: (float[])vmMemoryUsed;
- (void) readProcessorParameters;
@end

@implementation BacktraceAttributes

+ (BacktraceAttributes*)create {
    static BacktraceAttributes* instance = nil;
    if(!instance) {
        instance = [[BacktraceAttributes alloc] init];
    }
    return instance;
}

- (void) readMemoryParameters:  (float[])vmMemoryUsed {
    mach_port_t host_port;
    mach_msg_type_number_t host_size;
    vm_size_t pagesize;

    host_port = mach_host_self();
    host_size = sizeof(vm_statistics_data_t) / sizeof(integer_t);
    host_page_size(host_port, &pagesize);

    vm_statistics_data_t vm_stat;

    if (host_statistics(host_port, HOST_VM_INFO, (host_info_t)&vm_stat, &host_size) != KERN_SUCCESS) {
        NSLog(@"Failed to fetch vm statistics");
    }
    
    vmMemoryUsed[0] = vm_stat.active_count;
    NSLog(@"Active memory: %f", vmMemoryUsed[0]);

}
    
- (void) readProcessorParameters {
    NSLog(@"Reading processor parameters");
}
@end

void GetAttributes(float memoryUsed[]) {
    [[BacktraceAttributes create] readMemoryParameters:memoryUsed];
}
