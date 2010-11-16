package org.xerela.flex
{
	import mx.resources.ResourceManager;
	
	import org.xerela.flex.compare.DiffEditor;
	import org.xerela.flex.devices.Backup;
	import org.xerela.flex.devices.BackupJobEditor;
	import org.xerela.flex.devices.CreateBackupJob;
	import org.xerela.flex.devices.DeviceDetail;
	import org.xerela.flex.devices.Discovery;
	import org.xerela.flex.devices.NeighborsEditor;
	import org.xerela.flex.tools.CheckboxInputContributor;
	import org.xerela.flex.tools.ComboInputContributor;
	import org.xerela.flex.tools.CreateReportJob;
	import org.xerela.flex.tools.CreateToolJob;
	import org.xerela.flex.tools.DateTimeInputContributor;
	import org.xerela.flex.tools.GridInputContributor;
	import org.xerela.flex.tools.HiddenInputContributor;
	import org.xerela.flex.tools.IpAddressInputContributor;
	import org.xerela.flex.tools.ListInputContributor;
	import org.xerela.flex.tools.PasswordInputContributor;
	import org.xerela.flex.tools.RunTool;
	import org.xerela.flex.tools.StringInputContributor;
	import org.xerela.flex.tools.ToolJobEditor;
	import org.xerela.flex.tools.ToolOutputEditor;
	import org.xerela.flex.tools.ValidatePasswordInputContributor;
	import org.xerela.flex.tools.filestore.ToolStoreInputContributor;
	
	[ResourceBundle('messages')]
	public final class Registry
	{
		[Embed(source='/img/back2.png')]
		public static var backupPng:Class;
		[Embed(source='/img/find.png')]
		public static var deviceDiscoveryPng:Class;
		[Embed(source='/img/find_off.png')]
        public static var deviceDiscoveryOffPng:Class;
		[Embed(source='/wrench.png')]
		public static var wrenchPng:Class;
		[Embed(source='/img/wrench_off.png')]
        public static var wrenchOffPng:Class;
		[Embed(source='/report.png')]
        public static var reportPng:Class;
        [Embed(source='/img/restore.png')]
        public static var restorePng:Class;
        [Embed(source='/credentials.png')]
        public static var credsPng:Class;
        [Embed(source='/adapterdiagnostic.png')]
        public static var adapterDiagPng:Class;
        [Embed(source='/schedulerfilters.png')]
        public static var schedulerfilterPng:Class;
        [Embed(source='/discovery.png')]
        public static var discoveryPng:Class;
        [Embed(source='/adddevice.png')]
        public static var adddevicePng:Class;
		[Embed(source='/devicetags.png')]
        public static var devicetagsPng:Class;
        [Embed(source='/launcher.png')]
        public static var urllauncherPng:Class;
        [Embed(source='/protocols.png')]
        public static var protocolsPng:Class;
        [Embed(source='/cal.png')]
        public static var calPng:Class;        
        [Embed(source='/find.png')]
        public static var findPng:Class;
        [Embed(source='/report24.png')]
        public static var report24png:Class;
        [Bindable]
        [Embed(source='/img/openjob.png')]
        public static var openJobPng:Class;
		[Embed(source='/img/openjob_off.png')]
        public static var openJobOffPng:Class;
        [Bindable] 
        [Embed(source='/backup2.png')]
        public static var backup2Png:Class; 
                
		public static var displayBindingDefaults:Object = {
            'Device':'{hostname} - {ipAddress}',
            'Neighbors':'{hostname} - ' + ResourceManager.getInstance().getString('messages', 'Registry_neighbors'),
            'Config':'{device.hostname} - {revision.path}',
            'Job':'{jobGroup}/{jobName}',
            'Plugin Output':'{pluginDescriptor.toolName}',
            'Diff':ResourceManager.getInstance().getString('messages', 'Registry_diff')
        };

        public static var defaultDeviceColumns:Array = [
            {name:'backupStatus', width:25},
        	{name:'ipAddress', width:125},
        	{name:'hostname'},
        	{name:'adapterId'},
        	{name:'model'}
        ];

		public static var inputTypeContributors:Object = {
		    string: StringInputContributor,
		    ipAddress: IpAddressInputContributor,
		    password: PasswordInputContributor,
            passwordValidate: ValidatePasswordInputContributor,
            datetime: DateTimeInputContributor,
            checkbox: CheckboxInputContributor,
            hidden: HiddenInputContributor,
            combo: ComboInputContributor,
            list: ListInputContributor,
            grid: GridInputContributor,
            toolStoreBrowser: ToolStoreInputContributor
		};

        public static var jobTypes:Object = {
        	'Backup Configuration':{
        		icon:backupPng,
        		displayName:ResourceManager.getInstance().getString('messages', 'jobTypes_backup'),
        		create:CreateBackupJob.run,
        		schedulePermission:'org.xerela.job.backup.cudPermission',   
        		runPermission:'org.xerela.job.backup.runPermission',
        		runExisting: Backup.newFromExisting
            },
            'Discover Devices':{
                icon:deviceDiscoveryPng,
                displayName:ResourceManager.getInstance().getString('messages', 'jobTypes_discovery'),
                schedulePermission:'org.xerela.job.discovery.cudPermission',   
                runPermission:'org.xerela.job.discovery.runPermission',
                runExisting: Discovery.newFromExisting
            },
            'Script Tool Job':{
            	icon:wrenchPng,
            	displayName:ResourceManager.getInstance().getString('messages', 'jobTypes_tool'),
            	create:CreateToolJob.run,
            	schedulePermission:'org.xerela.job.plugin.cudPermission',   
                runPermission:'org.xerela.job.plugin.runPermission',
            	runExisting: RunTool.newFromExisting
            },
            'BIRT Report':{
                icon:reportPng,
                displayName:ResourceManager.getInstance().getString('messages', 'jobTypes_report'),
                create:CreateReportJob.run,
                schedulePermission:'org.xerela.job.plugin.cudPermission',   
                runPermission:'org.xerela.job.plugin.runPermission',
                runExisting: RunTool.newFromExisting
            },
            'Restore Configuration':{
                icon:restorePng,
                schedulePermission:'org.xerela.job.restore.cudPermission',   
                runPermission:'org.xerela.job.restore.runPermission',
                displayName:ResourceManager.getInstance().getString('messages', 'jobTypes_restore')
            }
        };

        public static var editors:Object = {
        	'Job:Script Tool Job':ToolJobEditor,
        	'Job:BIRT Report':ToolJobEditor,
        	'Job:Backup Configuration':BackupJobEditor,
        	'Device':DeviceDetail,
        	'Config':ConfigEditor,
        	'Neighbors':NeighborsEditor,
        	'Plugin Output':ToolOutputEditor,
        	'Diff':DiffEditor
        };

		public function Registry()
		{
		}
	}
}