create table sm_digit
(
    id                  bigint auto_increment
        primary key,
    service_instance_id bigint                              not null comment '服务实例ID',
    chunk               int                                 not null comment '服务实例本次获取ID的数量',
    from_value          bigint                              not null comment 'ID起始值(含括)',
    to_value            bigint                              not null comment 'ID结束值(含括)',
    status              tinyint   default 0                 null comment '状态(0:未使用;1:已使用)',
    gmt_created         timestamp default CURRENT_TIMESTAMP null,
    constraint sm_digit_pk
        unique (service_instance_id, from_value, to_value)
)
    comment '客户端获取ID记录';

create table sm_digit_used
(
    id                  bigint auto_increment
        primary key,
    service_instance_id bigint                              not null comment '服务实例ID',
    chunk               int                                 not null comment '服务实例本次获取ID的数量',
    from_value          bigint                              not null comment 'ID起始值(含括)',
    to_value            bigint                              not null comment 'ID结束值(含括)',
    status              tinyint   default 0                 null comment '状态(0:未使用;1:已使用)',
    gmt_created         timestamp default CURRENT_TIMESTAMP null,
    constraint sm_digit_pk
        unique (service_instance_id, from_value, to_value)
)
    comment '客户端获取ID已使用记录';

create table sm_distribution_lock
(
    id          bigint auto_increment
        primary key,
    lock_key    varchar(100)                        null comment '分布式锁key',
    lock_value  varchar(100)                        null comment '分布式锁value',
    expire_time bigint                              null comment '分布式锁过期时间',
    lock_owner  varchar(100)                        null comment '锁拥有者ip',
    version     int                                 null comment '乐观锁版本号',
    is_locked   tinyint   default 0                 null comment '0:无锁;1:已锁',
    gmt_created timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint sm_distribution_lock_key_uindex
        unique (lock_key)
)
    comment '分布式锁' charset = utf8mb4;

create table sm_group
(
    id           bigint auto_increment
        primary key,
    name         varchar(200)                        not null comment '服务名称',
    group_code   varchar(200)                        not null comment '服务组编号',
    chunk        int                                 not null comment '服务组每次获取ID数量',
    mode         int       default 1                 null comment '1:数字;2:雪花算法;3:UUID',
    `last_value` varchar(66)                         not null comment '服务组最近一次获取的ID最大值',
    gmt_created  timestamp                           null,
    gmt_updated  timestamp default CURRENT_TIMESTAMP null
)
    comment '客户端服务组';

create index sm_group_code_index
    on sm_group (group_code);

create table sm_service_instance
(
    id          bigint auto_increment
        primary key,
    group_id    bigint                              not null comment '服务组ID',
    server_code varchar(200)                        not null comment '服务编号',
    snow_times  int                                 not null comment '获取ID次数',
    status      tinyint   default 1                 not null comment '服务状态(1:在线;2:下线;3:未知)',
    gmt_created timestamp                           null,
    gmt_updated timestamp default CURRENT_TIMESTAMP null
)
    comment '客户端服务实例';

create index sm_instance_group_index
    on sm_service_instance (group_id, server_code);

create table sm_snowflake
(
    id                  bigint auto_increment
        primary key,
    service_instance_id bigint                              not null comment '服务实例ID',
    chunk               int                                 not null comment '服务实例本次获取ID的数量',
    g_value             bigint                              not null comment 'ID值',
    status              tinyint   default 0                 null comment '状态(0:未使用;1:已使用)',
    gmt_created         timestamp default CURRENT_TIMESTAMP null,
    constraint sm_snowflake_pk
        unique (service_instance_id, g_value)
)
    comment '客户端获取雪花ID记录';

create table sm_snowflake_used
(
    id                  bigint auto_increment
        primary key,
    service_instance_id bigint                              not null comment '服务实例ID',
    chunk               int                                 not null comment '服务实例本次获取ID的数量',
    g_value             bigint                              not null comment 'ID值',
    status              tinyint   default 0                 null comment '状态(0:未使用;1:已使用)',
    gmt_created         timestamp default CURRENT_TIMESTAMP null,
    constraint sm_snowflake_pk
        unique (service_instance_id, g_value)
)
    comment '客户端获取雪花ID已使用记录';

create table sm_timestamp
(
    id                  bigint auto_increment
        primary key,
    service_instance_id bigint                              not null comment '服务实例ID',
    chunk               int                                 not null comment '服务实例本次获取ID的数量',
    g_value             varchar(66)                         not null comment 'ID值',
    status              tinyint   default 0                 null comment '状态(0:未使用;1:已使用)',
    gmt_created         timestamp default CURRENT_TIMESTAMP null,
    constraint sm_timestamp_unique
        unique (service_instance_id, g_value)
)
    comment '客户端获取时间戳记录';

create table sm_timestamp_used
(
    id                  bigint auto_increment
        primary key,
    service_instance_id bigint                              not null comment '服务实例ID',
    chunk               int                                 not null comment '服务实例本次获取ID的数量',
    g_value             varchar(66)                         not null comment 'ID值',
    status              tinyint   default 0                 null comment '状态(0:未使用;1:已使用)',
    gmt_created         timestamp default CURRENT_TIMESTAMP null
)
    comment '客户端获取时间戳已使用记录';

create table sm_uuid
(
    id                  bigint auto_increment
        primary key,
    service_instance_id bigint                              not null comment '服务实例ID',
    chunk               int                                 not null comment '服务实例本次获取ID的数量',
    g_value             varchar(66)                         not null comment 'ID值',
    status              tinyint   default 0                 null comment '状态(0:未使用;1:已使用)',
    gmt_created         timestamp default CURRENT_TIMESTAMP null,
    constraint sm_uuid_pk
        unique (service_instance_id, g_value)
)
    comment '客户端获取UUID记录';

create table sm_uuid_used
(
    id                  bigint auto_increment
        primary key,
    service_instance_id bigint                              not null comment '服务实例ID',
    chunk               int                                 not null comment '服务实例本次获取ID的数量',
    g_value             varchar(66)                         not null comment 'ID值',
    status              tinyint   default 0                 null comment '状态(0:未使用;1:已使用)',
    gmt_created         timestamp default CURRENT_TIMESTAMP null,
    constraint sm_uuid_pk
        unique (service_instance_id, g_value)
)
    comment '客户端获取UUID已使用记录';

